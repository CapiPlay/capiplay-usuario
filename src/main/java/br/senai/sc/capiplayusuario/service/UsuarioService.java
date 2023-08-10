package br.senai.sc.capiplayusuario.service;


import br.senai.sc.capiplayusuario.exceptions.CadastroInvalidoException;
import br.senai.sc.capiplayusuario.exceptions.EmailEmUsoException;
import br.senai.sc.capiplayusuario.exceptions.UsuarioInexistenteException;

import br.senai.sc.capiplayusuario.model.dto.EditarUsuarioCommand;

import br.senai.sc.capiplayusuario.model.dto.UsuarioDTO;
import br.senai.sc.capiplayusuario.model.entity.Usuario;
import br.senai.sc.capiplayusuario.repository.UsuarioRepository;
import br.senai.sc.capiplayusuario.utils.GeradorUuidUtils;

import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Random;

import static org.springframework.beans.BeanUtils.copyProperties;


@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${diretorio-usuario}")
    public String diretorio;


    public Usuario salvar(UsuarioDTO usuarioDTO, byte[] bytes) {
        Usuario usuario = new Usuario();
        usuario.setFoto(salvarFoto(bytes, usuarioDTO.getPerfil()));
        return criarUsuario(usuarioDTO, usuario);
    }

    public Usuario buscarUm(String id) {
        return usuarioRepository
                .findById(id)
                .orElseThrow(UsuarioInexistenteException::new);
    }


    public void deletar(String id) {
        try {
            usuarioRepository.deleteById(id);
        } catch (Exception e) {
            throw new UsuarioInexistenteException();
        }
    }

    private Usuario criarUsuario(UsuarioDTO usuarioDTO, Usuario usuario) {
        if (validaIdade(usuarioDTO.getDataNascimento())) {
            copyProperties(usuarioDTO, usuario);
            usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
            usuario.setEnabled(true);
            try {
                return usuarioRepository.save(usuario);
            } catch (DataIntegrityViolationException e) {
                throw new EmailEmUsoException();
            } catch (Exception e) {
                throw new CadastroInvalidoException();
            }
        }
        return null;
    }

    public String salvarFoto(byte[] foto, String nome) {
        String uuid = GeradorUuidUtils.gerarUuid();
        File file = new File(diretorio + uuid + "_foto.png");
        try (FileOutputStream fos = new FileOutputStream(file)) {

            if (foto == null || foto.length == 0) {
                gerarFotoPadrao(nome, file);
                return file.getAbsolutePath();
            }

            fos.write(foto);

            BufferedImage imagemOriginal = ImageIO.read(file);

            if (imagemOriginal == null) {
                gerarFotoPadrao(nome, file);
                return file.getAbsolutePath();
            }

            int larguraDesejada = 176;
            int alturaDesejada = 176;
            Image imagemRedimensionada = imagemOriginal.getScaledInstance(larguraDesejada, alturaDesejada, Image.SCALE_SMOOTH);
            BufferedImage bufferedImagemRedimensionada = new BufferedImage(larguraDesejada, alturaDesejada, BufferedImage.TYPE_INT_RGB);
            bufferedImagemRedimensionada.getGraphics().drawImage(imagemRedimensionada, 0, 0, null);
            ImageIO.write(bufferedImagemRedimensionada, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
            return "Deu erro";
        }
        return file.getName();
    }

    public boolean existePorPerfil(String perfil) {
        return usuarioRepository.existsByPerfil(perfil);
    }

    public static void gerarFotoPadrao(String nomeUsuario, File arquivoImagemPadrao) throws IOException {

        int tamanhoQuadrado = 176;
        int tamanhoFonte = 88;

        BufferedImage imagemPadrao = new BufferedImage(tamanhoQuadrado, tamanhoQuadrado, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = imagemPadrao.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        Random random = new Random();
        Color corAleatoria = new Color(random.nextInt(231), random.nextInt(231), random.nextInt(231));
        g2d.setColor(corAleatoria);
        g2d.fillRect(0, 0, tamanhoQuadrado, tamanhoQuadrado);

        g2d.setColor(Color.WHITE);

        g2d.setFont(new Font("Arial", Font.PLAIN, tamanhoFonte));

        char primeiraLetra = nomeUsuario.toUpperCase().charAt(0);

        FontMetrics metrics = g2d.getFontMetrics();
        int x = (tamanhoQuadrado - metrics.charWidth(primeiraLetra)) / 2;
        int y = ((tamanhoQuadrado - metrics.getHeight()) / 2) + metrics.getAscent();

        g2d.drawString(String.valueOf(primeiraLetra), x, y);

        g2d.dispose();

        ImageIO.write(imagemPadrao, "png", arquivoImagemPadrao);
    }

    public String nomePadrao(String nomePadrao, String id) {

        String nomeFinal = nomePadrao;

        Set<String> users = usuarioRepository.findAllByPerfil(nomePadrao, id);

        for (int i = 0; users.contains(nomeFinal); i++) {
            nomeFinal = nomePadrao + "_" + i;
        }
        return nomeFinal;

    }

    public boolean validaIdade(Date dataNascimento) {

        Calendar dataNascimentoCal = Calendar.getInstance();
        dataNascimentoCal.setTime(dataNascimento);

        Calendar dataAtualCal = Calendar.getInstance();

        int idade = dataAtualCal.get(Calendar.YEAR) - dataNascimentoCal.get(Calendar.YEAR);


        if (dataNascimentoCal.get(Calendar.MONTH) > dataAtualCal.get(Calendar.MONTH) ||
                (dataNascimentoCal.get(Calendar.MONTH) == dataAtualCal.get(Calendar.MONTH) &&
                        dataNascimentoCal.get(Calendar.DAY_OF_MONTH) > dataAtualCal.get(Calendar.DAY_OF_MONTH) ||
                        dataNascimentoCal.get(Calendar.DAY_OF_MONTH) == dataAtualCal.get(Calendar.DAY_OF_MONTH))) {
            idade--;
        }

        return idade > 6 && idade < 150;
    }


    public Usuario handle(@Valid EditarUsuarioCommand cmd, String id) {
        Usuario usuario = buscarUm(id);

        if (existePorPerfil(cmd.getPerfil())) {
            cmd.setPerfil(nomePadrao(cmd.getPerfil(), id));
        }

        salvarFoto(cmd.getFoto(), cmd.getNome());

        copyProperties(cmd, usuario);

        usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }
}

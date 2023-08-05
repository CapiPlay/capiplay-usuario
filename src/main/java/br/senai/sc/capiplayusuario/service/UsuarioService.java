package br.senai.sc.capiplayusuario.service;

import br.senai.sc.capiplayusuario.exceptions.UsuarioInexistente;
import br.senai.sc.capiplayusuario.model.dto.UsuarioDTO;
import br.senai.sc.capiplayusuario.model.entity.Usuario;
import br.senai.sc.capiplayusuario.repository.UsuarioRepository;
import br.senai.sc.capiplayusuario.utils.GeradorUuidUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${diretorio-usuario}")
    public String diretorio;

    public boolean salvar(UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        return criarUsuario(usuarioDTO, usuario);
    }

    public boolean editar(UsuarioDTO usuarioDTO, String id) {
        Usuario usuario = buscarUm(id);
        return criarUsuario(usuarioDTO, usuario);
    }

    public Usuario buscarUm(String id) {
        return usuarioRepository
                .findById(id)
                .orElseThrow(UsuarioInexistente::new);
    }

    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }

    public void deletar(String id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    private boolean criarUsuario(UsuarioDTO usuarioDTO, Usuario usuario) {
        if (!validaIdade(usuarioDTO.getDataNascimento())) {
            BeanUtils.copyProperties(usuarioDTO, usuario);
            usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
            usuarioRepository.save(usuario);
            return false;
        }
        return false;
    }

    public String salvarFoto(MultipartFile multipartFile, String nome) {
        String uuid = GeradorUuidUtils.gerarUuid();
        File file = new File(diretorio + uuid + "_foto.png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());

            BufferedImage imagemOriginal = ImageIO.read(file);

            if (imagemOriginal == null) {
                gerarFotoPadrao(nome, file);
            } else {

                int larguraDesejada = 176;
                int alturaDesejada = 176;

                Image imagemRedimensionada = imagemOriginal.getScaledInstance(larguraDesejada, alturaDesejada, Image.SCALE_SMOOTH);
                BufferedImage bufferedImagemRedimensionada = new BufferedImage(larguraDesejada, alturaDesejada, BufferedImage.TYPE_INT_RGB);

                bufferedImagemRedimensionada.getGraphics().drawImage(imagemRedimensionada, 0, 0, null);

                ImageIO.write(bufferedImagemRedimensionada, "png", file);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "Deu erro";
        }
        return file.getAbsolutePath();
    }

    public Usuario buscarPorPerfil(String perfil) {
        return usuarioRepository.findByPerfil(perfil);
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

    public String nomePadrao(String email) {
        int indexArroba = email.indexOf('@');
        if (indexArroba != -1) {
            String nomePadrao = email.substring(0, indexArroba).trim();

            String nomeFinal = nomePadrao;

            Set<String> users = usuarioRepository.findAllByPerfil(nomePadrao);

            for (int i = 0; users.contains(nomeFinal); i++) {
                nomeFinal = nomePadrao + "_" + i;
            }
            return nomeFinal;
        } else {
            return null;
        }
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


}

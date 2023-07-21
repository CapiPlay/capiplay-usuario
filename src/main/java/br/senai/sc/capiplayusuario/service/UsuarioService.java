package br.senai.sc.capiplayusuario.service;

import br.senai.sc.capiplayusuario.exceptions.UsuarioInexistente;
import br.senai.sc.capiplayusuario.model.dto.UsuarioDTO;
import br.senai.sc.capiplayusuario.model.entity.Usuario;
import br.senai.sc.capiplayusuario.repository.UsuarioRepository;
import br.senai.sc.capiplayusuario.utils.GeradorUuidUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
//@AllArgsConstructor
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${diretorio-usuario}")
    public String diretorio;

    public void salvar(UsuarioDTO usuarioDTO){
        Usuario usuario = new Usuario();
        criarUsuario(usuarioDTO,usuario);
    }

    public void editar(UsuarioDTO usuarioDTO, String id) {
        Usuario usuario = buscarUm(id);
        criarUsuario(usuarioDTO, usuario);
    }
    public Usuario buscarUm(String id){
        return usuarioRepository
                .findById(id)
                .orElseThrow(UsuarioInexistente::new);
    }

    public List<Usuario> buscarTodos(){
        return usuarioRepository.findAll();
    }

    public void deletar(String id){
        usuarioRepository.deleteById(id);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    private void criarUsuario(UsuarioDTO usuarioDTO, Usuario usuario) {
        BeanUtils.copyProperties(usuarioDTO, usuario);
        usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
    }

    public String salvarFoto(MultipartFile multipartFile) {
        String uuid = GeradorUuidUtils.gerarUuid();
        File file = new File(diretorio + uuid + "_foto.jpeg");
        try (FileOutputStream fos = new FileOutputStream(file);){
            fos.write(multipartFile.getBytes());

            BufferedImage imagemOriginal = ImageIO.read(file);

            int larguraDesejada = 176;
            int alturaDesejada = 176;

            Image imagemRedimensionada = imagemOriginal.getScaledInstance(larguraDesejada, alturaDesejada, Image.SCALE_SMOOTH);
            BufferedImage bufferedImagemRedimensionada = new BufferedImage(larguraDesejada, alturaDesejada, BufferedImage.TYPE_INT_RGB);

            bufferedImagemRedimensionada.getGraphics().drawImage(imagemRedimensionada, 0, 0, null);

            ImageIO.write(bufferedImagemRedimensionada, "jpeg", file);
        } catch (IOException e) {
            e.printStackTrace();
            return "Deu erro";
        }
        return "Deu certo";
    }

    public Usuario buscarPorPerfil(String perfil) {
        return usuarioRepository.findByPerfil(perfil);
    }
}

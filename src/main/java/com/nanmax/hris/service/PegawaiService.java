package com.nanmax.hris.service;
import com.nanmax.hris.entity.Pegawai;
import com.nanmax.hris.repository.PegawaiRepository;
import com.nanmax.hris.web.ApiBusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Service
public class PegawaiService {
    private final PegawaiRepository repo;
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    public PegawaiService(PegawaiRepository repo) { 
        this.repo = repo; 
    }
    public Pegawai createPegawai(Pegawai p) {
        if (p.getCreatedAt() == null) p.setCreatedAt(Instant.now());
        return repo.save(p);
    }
    public Pegawai updatePegawai(Pegawai p) {
        return repo.save(p);
    }
    public List<Pegawai> getAllPegawai() { 
        return repo.findAll(); 
    }
    public Pegawai findByEmail(String email) { 
        return repo.findByEmail(email); 
    }
    public Pegawai findByIdUser(String idUser) { 
        return repo.findByIdUser(idUser); 
    }
    public List<Pegawai> findByProfile(String profile) {
        return repo.findByProfile(profile);
    }
    public List<Pegawai> findByDepartemenName(String departemenName) {
        return repo.findByDepartemen_NamaDepartemen(departemenName);
    }
    public void setPegawaiMasterData(Pegawai pegawai, Integer kdJabatan, Integer kdDepartemen, 
                                   Integer kdUnitKerja, Integer kdJenisKelamin, Integer kdPendidikan) {
    }
    public String savePhoto(MultipartFile file, String originalFileName) {
        try {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new ApiBusinessException("File harus berupa gambar!");
            }
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileExtension = getFileExtension(originalFileName);
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath);
            return "/uploads/" + uniqueFileName;
        } catch (IOException e) {
            throw new ApiBusinessException("Gagal menyimpan file: " + e.getMessage());
        }
    }
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }
}
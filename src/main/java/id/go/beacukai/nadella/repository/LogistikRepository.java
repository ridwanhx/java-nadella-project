package id.go.beacukai.nadella.repository;

import id.go.beacukai.nadella.entity.Logistik;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LogistikRepository extends JpaRepository<Logistik, Long> {
    Optional<Logistik> findByKodeLogistik(String kodeLogistik);
    boolean existsByKodeLogistik(String kodeLogistik);
    
    Page<Logistik> findAll(Pageable pageable);
    
    Page<Logistik> findByKodeLogistikContainingIgnoreCase(String kodeLogistik, Pageable pageable);
    
    @Query("SELECT l FROM Logistik l WHERE " +
           "LOWER(l.kodeLogistik) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(l.namaBarang) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(CAST(l.jenisKegiatan AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(CAST(l.jalurPemeriksaan AS string)) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Logistik> advancedSearch(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT COUNT(l) FROM Logistik l WHERE l.jenisKegiatan = 'IMPOR'")
    long countImpor();
    
    @Query("SELECT COUNT(l) FROM Logistik l WHERE l.jenisKegiatan = 'EKSPOR'")
    long countEkspor();
    
    @Query("SELECT COUNT(l) FROM Logistik l WHERE l.jalurPemeriksaan = 'HIJAU'")
    long countJalurHijau();
    
    @Query("SELECT COUNT(l) FROM Logistik l WHERE l.jalurPemeriksaan = 'KUNING'")
    long countJalurKuning();
    
    @Query("SELECT COUNT(l) FROM Logistik l WHERE l.jalurPemeriksaan = 'MERAH'")
    long countJalurMerah();
}

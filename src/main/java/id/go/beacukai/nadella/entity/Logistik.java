package id.go.beacukai.nadella.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import id.go.beacukai.nadella.entity.Logistik.PelabuhanBandara;

@Entity
@Table(name = "logistik")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Logistik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name = "kode_logistik")
    @Pattern(regexp = "^NDL-BC-\\d{4}-\\d{4}$", message = "Format Kode Logistik harus: NDL-BC-XXXX-YYYY (contoh: NDL-BC-2045-2025)")
    private String kodeLogistik;

    @Column(nullable = false, name = "nama_barang")
    @NotBlank(message = "Nama barang tidak boleh kosong")
    private String namaBarang;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "jenis_kegiatan")
    private JenisKegiatan jenisKegiatan;

    @Column(nullable = false, name = "negara")
    private String negara;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "pelabuhan_bandara")
    private PelabuhanBandara pelabuhanBandara;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "jalur_pemeriksaan")
    private JalurPemeriksaan jalurPemeriksaan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status_kepabeanan")
    private StatusKepabeanan statusKepabeanan;

    @Column(nullable = false, name = "nomor_dokumen")
    @Pattern(regexp = "^\\d{12}$", message = "Nomor dokumen harus terdiri dari 12 digit angka")
    private String nomorDokumen;

    @Column(nullable = false, name = "email_petugas")
    @Email(message = "Format email tidak valid")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@nadella\\.beacukai\\.go\\.id$", message = "Email harus menggunakan domain @nadella.beacukai.go.id")
    private String emailPetugas;

    @Column(nullable = false, name = "tanggal_pendaftaran")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalPendaftaran;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum JenisKegiatan {
        IMPOR("Impor"),
        EKSPOR("Ekspor");

        private final String displayName;

        JenisKegiatan(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum PelabuhanBandara {
        TANJUNG_PRIOK("Pelabuhan Tanjung Priok - Jakarta"),
        SUNDA_KELAPA("Pelabuhan Sunda Kelapa - Jakarta"),
        MERAK("Pelabuhan Merak - Banten"),
        TANJUNG_PERAK("Pelabuhan Tanjung Perak - Jawa  Timur"),
        SOEKARNO_HATTA("Pelabuhan Soekarno-Hatta - Sulawesi Selatan"),
        BAKAUHENI("Pelabuhan Bakauheni - Lampung"),
        HARBOUR_BAY("Pelabuhan Harbour Bay - Kepulauan Riau"),
        BATAM_CENTER("Pelabuhan Batam Center - Kepulauan Riau"),
        SULTAN_ISKANDAR_MUDA("Bandara Sultan Iskandar Muda - Aceh"),
        KUALANAMU("Bandara Kualanamu - Sumatera Utara"),
        MINANGKABAU("Bandara Minangkabau - Sumatera Barat"),
        HANG_NADIM("Bandara Hang Nadim - Kepulauan Riau"),
        HALIM_PERDANAKUSUMA("Bandara Halim Perdanakusuma - Jakarta"),
        SOEKARNOHATTA("Bandara Soekarno-Hatta - Banten");

        private final String displayName;

        PelabuhanBandara(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum JalurPemeriksaan {
        HIJAU("Hijau"),
        KUNING("Kuning"),
        MERAH("Merah");

        private final String displayName;

        JalurPemeriksaan(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum StatusKepabeanan {
        DALAM_PROSES("Dalam Proses"),
        SELESAI("Selesai"),
        PENDING("Pending"),
        DITOLAK("Ditolak");

        private final String displayName;

        StatusKepabeanan(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

}

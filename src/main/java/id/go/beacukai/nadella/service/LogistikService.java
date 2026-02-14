package id.go.beacukai.nadella.service;

import id.go.beacukai.nadella.entity.Logistik;
import id.go.beacukai.nadella.repository.LogistikRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogistikService {
    
    private final LogistikRepository logistikRepository;
    
    public List<Logistik> findAll() {
        return logistikRepository.findAll();
    }
    
    public Page<Logistik> findAll(Pageable pageable) {
        return logistikRepository.findAll(pageable);
    }
    
    public Page<Logistik> searchByKodeLogistik(String kodeLogistik, Pageable pageable) {
        return logistikRepository.findByKodeLogistikContainingIgnoreCase(kodeLogistik, pageable);
    }
    
    public Page<Logistik> advancedSearch(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return logistikRepository.findAll(pageable);
        }
        return logistikRepository.advancedSearch(search.trim(), pageable);
    }
    
    public Optional<Logistik> findById(Long id) {
        return logistikRepository.findById(id);
    }
    
    public Optional<Logistik> findByKodeLogistik(String kodeLogistik) {
        return logistikRepository.findByKodeLogistik(kodeLogistik);
    }
    
    @Transactional
    public Logistik save(Logistik logistik) {
        return logistikRepository.save(logistik);
    }
    
    @Transactional
    public void deleteById(Long id) {
        logistikRepository.deleteById(id);
    }
    
    public boolean existsByKodeLogistik(String kodeLogistik) {
        return logistikRepository.existsByKodeLogistik(kodeLogistik);
    }
    
    public long count() {
        return logistikRepository.count();
    }
    
    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalImpor", logistikRepository.countImpor());
        stats.put("totalEkspor", logistikRepository.countEkspor());
        stats.put("jalurHijau", logistikRepository.countJalurHijau());
        stats.put("jalurKuning", logistikRepository.countJalurKuning());
        stats.put("jalurMerah", logistikRepository.countJalurMerah());
        stats.put("totalData", logistikRepository.count());
        return stats;
    }
}

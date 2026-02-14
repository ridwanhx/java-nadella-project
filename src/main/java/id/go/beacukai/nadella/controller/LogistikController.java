package id.go.beacukai.nadella.controller;

import id.go.beacukai.nadella.entity.Logistik;
import id.go.beacukai.nadella.service.LogistikService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/logistik")
@RequiredArgsConstructor
public class LogistikController {

    private final LogistikService logistikService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Logistik> logistikPage;

        if (search != null && !search.isEmpty()) {
            logistikPage = logistikService.advancedSearch(search, pageable);
            model.addAttribute("search", search);
        } else {
            logistikPage = logistikService.findAll(pageable);
        }

        model.addAttribute("logistikPage", logistikPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", logistikPage.getTotalPages());
        model.addAttribute("totalItems", logistikPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "logistik/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("logistik", new Logistik());
        model.addAttribute("jenisKegiatanList", Logistik.JenisKegiatan.values());
        model.addAttribute("pelabuhanBandaraList", Logistik.PelabuhanBandara.values());
        model.addAttribute("jalurPemeriksaanList", Logistik.JalurPemeriksaan.values());
        model.addAttribute("statusKepabeananList", Logistik.StatusKepabeanan.values());
        return "logistik/form";
    }

    @PostMapping("/create")
    public String store(@Valid @ModelAttribute Logistik logistik,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("jenisKegiatanList", Logistik.JenisKegiatan.values());
            model.addAttribute("pelabuhanBandaraList", Logistik.PelabuhanBandara.values());
            model.addAttribute("jalurPemeriksaanList", Logistik.JalurPemeriksaan.values());
            model.addAttribute("statusKepabeananList", Logistik.StatusKepabeanan.values());
            return "logistik/form";
        }

        if (logistikService.existsByKodeLogistik(logistik.getKodeLogistik())) {
            model.addAttribute("error", "Kode Logistik sudah digunakan!");
            model.addAttribute("jenisKegiatanList", Logistik.JenisKegiatan.values());
            model.addAttribute("pelabuhanBandaraList", Logistik.PelabuhanBandara.values());
            model.addAttribute("jalurPemeriksaanList", Logistik.JalurPemeriksaan.values());
            model.addAttribute("statusKepabeananList", Logistik.StatusKepabeanan.values());
            return "logistik/form";
        }

        logistik.setCreatedBy(authentication.getName());
        logistikService.save(logistik);

        redirectAttributes.addFlashAttribute("success", "Data logistik berhasil ditambahkan!");
        return "redirect:/logistik";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return logistikService.findById(id)
                .map(logistik -> {
                    model.addAttribute("logistik", logistik);
                    model.addAttribute("jenisKegiatanList", Logistik.JenisKegiatan.values());
                    model.addAttribute("pelabuhanBandaraList", Logistik.PelabuhanBandara.values());
                    model.addAttribute("jalurPemeriksaanList", Logistik.JalurPemeriksaan.values());
                    model.addAttribute("statusKepabeananList", Logistik.StatusKepabeanan.values());
                    return "logistik/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Data tidak ditemukan!");
                    return "redirect:/logistik";
                });
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute Logistik logistik,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("jenisKegiatanList", Logistik.JenisKegiatan.values());
            model.addAttribute("pelabuhanBandaraList", Logistik.PelabuhanBandara.values());
            model.addAttribute("jalurPemeriksaanList", Logistik.JalurPemeriksaan.values());
            model.addAttribute("statusKepabeananList", Logistik.StatusKepabeanan.values());
            return "logistik/form";
        }

        return logistikService.findById(id)
                .map(existing -> {
                    logistik.setId(id);
                    logistik.setCreatedBy(existing.getCreatedBy());
                    logistik.setCreatedAt(existing.getCreatedAt());
                    logistikService.save(logistik);
                    redirectAttributes.addFlashAttribute("success", "Data logistik berhasil diupdate!");
                    return "redirect:/logistik";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Data tidak ditemukan!");
                    return "redirect:/logistik";
                });
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            logistikService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Data logistik berhasil dihapus!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus data!");
        }
        return "redirect:/logistik";
    }

    @GetMapping("/search")
    public String search(@RequestParam String kodeLogistik, RedirectAttributes redirectAttributes) {
        return logistikService.findByKodeLogistik(kodeLogistik)
                .map(logistik -> "redirect:/logistik/edit/" + logistik.getId())
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error",
                            "Data dengan kode " + kodeLogistik + " tidak ditemukan!");
                    return "redirect:/logistik";
                });
    }
}

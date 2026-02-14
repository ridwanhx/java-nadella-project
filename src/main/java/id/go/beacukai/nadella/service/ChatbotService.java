package id.go.beacukai.nadella.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${mistral.api.url}")
    private String apiUrl;
    
    @Value("${mistral.api.key}")
    private String apiKey;
    
    @Value("${mistral.api.model}")
    private String model;
    
    public String getResponse(String userMessage) {
        // Jika API key tidak dikonfigurasi, gunakan fallback
        if (apiKey == null || apiKey.equals("YOUR_MISTRAL_API_KEY_HERE") || apiKey.trim().isEmpty()) {
            log.warn("Mistral API key not configured, using fallback responses");
            return getFallbackResponse(userMessage);
        }
        
        try {
            WebClient webClient = webClientBuilder.build();
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(
                Map.of(
                    "role", "system",
                    "content", getSystemPrompt()
                ),
                Map.of(
                    "role", "user",
                    "content", userMessage
                )
            ));
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.7);
            
            log.info("Sending request to Mistral AI API...");
            
            Mono<Map> responseMono = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(30));
            
            Map responseMap = responseMono.block();
            
            if (responseMap != null && responseMap.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    log.info("Successfully received response from Mistral AI");
                    return content;
                }
            }
            
            log.warn("Invalid response format from Mistral API, using fallback");
            return getFallbackResponse(userMessage);
            
        } catch (WebClientResponseException e) {
            log.error("Mistral API error - Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return getFallbackResponse(userMessage);
        } catch (Exception e) {
            log.error("Error calling Mistral API: ", e);
            return getFallbackResponse(userMessage);
        }
    }
    
    private String getFallbackResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();
        
        // Fallback responses berdasarkan keyword
        if (lowerMessage.contains("jalur hijau") || lowerMessage.contains("green lane")) {
            return """
                Jalur Hijau adalah jalur pemeriksaan kepabeanan untuk barang yang dianggap tidak berisiko tinggi.
                
                Karakteristik Jalur Hijau:
                • Pemeriksaan minimal atau tanpa pemeriksaan fisik
                • Barang dapat langsung keluar dari kawasan pabean
                • Dokumen sudah diverifikasi secara elektronik
                • Cocok untuk importir/eksportir dengan rekam jejak baik
                
                Keuntungan:
                ✓ Proses clearance lebih cepat
                ✓ Biaya operasional lebih rendah
                ✓ Efisiensi waktu pengiriman
                """;
        }
        
        if (lowerMessage.contains("jalur kuning") || lowerMessage.contains("yellow lane")) {
            return """
                Jalur Kuning adalah jalur pemeriksaan kepabeanan dengan pemeriksaan dokumen yang lebih detail.
                
                Karakteristik Jalur Kuning:
                • Pemeriksaan dokumen secara menyeluruh
                • Verifikasi kelengkapan administrasi
                • Tidak ada pemeriksaan fisik barang (kecuali ada indikasi)
                • Waktu proses lebih lama dari jalur hijau
                
                Dokumen yang diperiksa:
                ✓ Invoice dan Packing List
                ✓ Bill of Lading / Airway Bill
                ✓ Certificate of Origin
                ✓ Pemberitahuan Impor Barang (PIB)
                """;
        }
        
        if (lowerMessage.contains("jalur merah") || lowerMessage.contains("red lane")) {
            return """
                Jalur Merah adalah jalur pemeriksaan kepabeanan dengan pemeriksaan paling ketat.
                
                Karakteristik Jalur Merah:
                • Pemeriksaan fisik barang secara menyeluruh
                • Pemeriksaan dokumen lengkap dan detail
                • Waktu proses paling lama
                • Untuk barang berisiko tinggi atau importir baru
                
                Yang diperiksa:
                ✓ Kesesuaian barang dengan dokumen
                ✓ Klasifikasi dan nilai barang
                ✓ Kelengkapan perizinan khusus
                ✓ Compliance dengan regulasi
                
                Barang akan dibuka dan diperiksa secara fisik oleh petugas Bea Cukai.
                """;
        }
        
        if (lowerMessage.contains("impor") || lowerMessage.contains("import")) {
            return """
                Proses Impor Barang di Indonesia:
                
                1. Persiapan Dokumen:
                   • Invoice
                   • Packing List
                   • Bill of Lading / AWB
                   • Certificate of Origin
                   
                2. Pemberitahuan Impor:
                   • Mengajukan PIB (Pemberitahuan Impor Barang)
                   • Sistem akan tentukan jalur pemeriksaan
                   
                3. Pemeriksaan:
                   • Jalur Hijau: Minimal/tanpa pemeriksaan
                   • Jalur Kuning: Pemeriksaan dokumen
                   • Jalur Merah: Pemeriksaan fisik
                   
                4. Pembayaran:
                   • Bea Masuk
                   • PPN dan/atau PPnBM
                   • Biaya lainnya
                   
                5. Pengeluaran Barang:
                   • Setelah clearance selesai
                """;
        }
        
        if (lowerMessage.contains("ekspor") || lowerMessage.contains("export")) {
            return """
                Proses Ekspor Barang dari Indonesia:
                
                1. Persiapan Barang dan Dokumen:
                   • Invoice
                   • Packing List
                   • Certificate of Origin (jika diperlukan)
                   
                2. Pemberitahuan Ekspor:
                   • Mengajukan PEB (Pemberitahuan Ekspor Barang)
                   • Registrasi online melalui sistem
                   
                3. Pemeriksaan Pabean:
                   • Pemeriksaan dokumen
                   • Pemeriksaan fisik (jika diperlukan)
                   
                4. Muat Barang:
                   • Setelah mendapat persetujuan
                   • Loading ke kapal/pesawat
                   
                Catatan: Beberapa barang ekspor memerlukan izin khusus dari Kementerian terkait.
                """;
        }
        
        if (lowerMessage.contains("dokumen") || lowerMessage.contains("document")) {
            return """
                Dokumen Kepabeanan yang Umum Diperlukan:
                
                Untuk Impor:
                ✓ PIB (Pemberitahuan Impor Barang)
                ✓ Invoice (Faktur)
                ✓ Packing List
                ✓ Bill of Lading / Airway Bill
                ✓ Certificate of Origin
                ✓ Izin khusus (jika barang tertentu)
                
                Untuk Ekspor:
                ✓ PEB (Pemberitahuan Ekspor Barang)
                ✓ Invoice
                ✓ Packing List
                ✓ Certificate of Origin (jika diminta negara tujuan)
                
                Semua dokumen harus lengkap dan akurat untuk menghindari penundaan clearance.
                """;
        }
        
        if (lowerMessage.contains("nomor dokumen") || lowerMessage.contains("mengisi nomor")) {
            return """
                Cara Mengisi Nomor Dokumen di Sistem Nadella:
                
                Format Nomor Dokumen: 12 digit angka
                Contoh: 123456789012
                
                Ketentuan:
                • Harus tepat 12 digit
                • Hanya boleh angka (0-9)
                • Tidak boleh ada huruf atau karakter khusus
                • Tidak boleh ada spasi
                
                Nomor dokumen ini biasanya merujuk pada:
                - Nomor PIB (untuk impor)
                - Nomor PEB (untuk ekspor)
                - Nomor referensi internal
                
                Pastikan nomor dokumen sudah benar sebelum menyimpan data.
                """;
        }
        
        // Default fallback
        return """
            Terima kasih atas pertanyaan Anda!
            
            Saya adalah Chatbot Bea Cukai Nadella yang dapat membantu Anda dengan informasi seputar:
            
            📌 Jalur Pemeriksaan (Hijau, Kuning, Merah)
            📌 Proses Impor dan Ekspor
            📌 Dokumen Kepabeanan
            📌 Prosedur dan Regulasi Bea Cukai
            
            Silakan ajukan pertanyaan lebih spesifik, misalnya:
            • "Apa itu jalur hijau?"
            • "Bagaimana proses impor barang?"
            • "Dokumen apa yang diperlukan untuk ekspor?"
            
            Catatan: Saat ini menggunakan mode demo. Untuk jawaban AI yang lebih canggih, 
            silakan konfigurasi Mistral API key di application.properties.
            """;
    }
    
    private String getSystemPrompt() {
        return """
            Anda adalah asisten chatbot untuk sistem Bea Cukai Nadella Indonesia.
            Tugas Anda adalah menjawab pertanyaan seputar:
            1. Jalur pemeriksaan kepabeanan (Hijau, Kuning, Merah)
            2. Proses impor dan ekspor barang
            3. Dokumen kepabeanan yang diperlukan
            4. Prosedur dan regulasi Bea Cukai Indonesia
            
            Informasi Jalur Pemeriksaan:
            - JALUR HIJAU: Untuk barang yang tidak berisiko, pemeriksaan minimal, bisa langsung keluar
            - JALUR KUNING: Pemeriksaan dokumen lebih detail, verifikasi kelengkapan administrasi
            - JALUR MERAH: Pemeriksaan fisik barang dan dokumen secara menyeluruh
            
            Berikan jawaban yang informatif, profesional, dan membantu dalam bahasa Indonesia.
            Jika pertanyaan di luar lingkup Bea Cukai, beritahu pengguna dengan sopan bahwa Anda hanya dapat membantu dengan pertanyaan terkait kepabeanan.
            """;
    }
}

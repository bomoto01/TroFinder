package com.trofinder.ai.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class LocalRuleBasedAiClient implements AiClient {

    @Override
    public String generateResponse(String systemPrompt, String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            return "Xin chào! Tôi là trợ lý ảo TroFinder. Bạn cần tìm phòng trọ ở khu vực nào, tầm giá bao nhiêu hoặc có câu hỏi gì cần hỗ trợ?";
        }

        String lower = userMessage.toLowerCase().trim();
        String context = systemPrompt != null ? systemPrompt : "";

        // Trích xuất các thông tin phòng nếu có trong systemPrompt
        Double area = extractArea(context);
        String address = extractGroup(context, "Địa chỉ:\\s*'([^']+)'");
        String amenities = extractGroup(context, "Tiện ích có sẵn:\\s*([^.]+)");
        String contact = extractGroup(context, "Người liên hệ:\\s*([^(]+)\\s*\\(SĐT:\\s*([^)]+)\\)");
        String price = extractGroup(context, "Giá thuê:\\s*([0-9.]+)\\s*VNĐ");
        String deposit = extractGroup(context, "Tiền cọc:\\s*([0-9.]+)\\s*VNĐ");
        String electric = extractGroup(context, "Giá điện:\\s*([0-9.]+)\\s*VNĐ");
        String water = extractGroup(context, "Giá nước:\\s*([0-9.]+)\\s*VNĐ");

        // 1. Hỏi về số người ở được
        if (lower.contains("bao nhiêu người") || lower.contains("mấy người") || lower.contains("ở được mấy")
                || lower.contains("số người") || lower.contains("ở chung") || lower.contains("ghép")) {
            if (area != null) {
                if (area < 20) {
                    return String.format(
                            "Căn phòng này có diện tích khoảng %.0f m², thích hợp nhất cho 1 - 2 người ở để đảm bảo không gian học tập và sinh hoạt thoải mái.",
                            area);
                } else if (area < 35) {
                    return String.format(
                            "Căn phòng này có diện tích %.0f m² khá rộng rãi, rất phù hợp cho 2 - 3 người (hoặc nhóm sinh viên, gia đình nhỏ) cùng ở.",
                            area);
                } else {
                    return String.format(
                            "Căn phòng này có diện tích rộng rãi tới %.0f m², hoàn toàn có thể ở thoải mái từ 3 - 4 người.",
                            area);
                }
            }
            return "Thông thường các phòng trọ khép kín phù hợp cho 2 - 3 người ở. Bạn có thể tham khảo diện tích cụ thể của phòng hoặc liên hệ trực tiếp chủ trọ để trao đổi số lượng người ở phù hợp.";
        }

        // 2. Hỏi về nội thất, tiện ích
        if (lower.contains("nội thất") || lower.contains("tiện ích") || lower.contains("có gì")
                || lower.contains("đồ đạc") || lower.contains("điều hòa") || lower.contains("máy giặt")
                || lower.contains("tủ lạnh") || lower.contains("nóng lạnh")) {
            if (amenities != null && !amenities.isBlank()) {
                return String.format("Căn phòng này được trang bị các tiện ích: %s. Bạn có thể liên hệ chủ phòng để kiểm tra thực tế khi đến xem phòng.", amenities);
            }
            return "Căn phòng có các tiện ích cơ bản được chủ trọ liệt kê trong phần 'Tiện ích phòng'. Hãy liên hệ chủ nhà để biết thêm thông tin chi tiết.";
        }

        // 3. Hỏi về giá cả, chi phí, cọc, điện nước
        if (lower.contains("giá") || lower.contains("bao nhiêu tiền") || lower.contains("chi phí")
                || lower.contains("tiền phòng") || lower.contains("tiền cọc") || lower.contains("đặt cọc")
                || lower.contains("tiền điện") || lower.contains("tiền nước")) {
            if (price != null) {
                StringBuilder costInfo = new StringBuilder(String.format("Giá thuê phòng hiện tại là %s đ/tháng", formatCurrency(price)));
                if (deposit != null) {
                    costInfo.append(String.format(", tiền đặt cọc là %s đ", formatCurrency(deposit)));
                }
                if (electric != null) {
                    costInfo.append(String.format(", giá điện %s đ/số", formatCurrency(electric)));
                }
                if (water != null) {
                    costInfo.append(String.format(", giá nước %s đ", formatCurrency(water)));
                }
                costInfo.append(".");
                return costInfo.toString();
            }
            return "Giá thuê và chi phí dịch vụ (điện, nước, cọc) được niêm yết minh bạch trên trang tin. Bạn nên kiểm tra kỹ các khoản phí trước khi ký hợp đồng.";
        }

        // 4. Hỏi về địa chỉ, vị trí
        if (lower.contains("địa chỉ") || lower.contains("ở đâu") || lower.contains("vị trí") || lower.contains("khu vực")) {
            if (address != null && !address.isBlank()) {
                return String.format("Căn phòng tọa lạc tại địa chỉ: %s.", address);
            }
            return "Bạn có thể xem vị trí cụ thể của phòng trên bản đồ tương tác của TroFinder hoặc liên hệ chủ trọ để được chỉ đường chi tiết.";
        }

        // 5. Hỏi về chủ nhà, liên hệ
        if (lower.contains("chủ nhà") || lower.contains("liên hệ") || lower.contains("số điện thoại")
                || lower.contains("sđt") || lower.contains("gọi điện") || lower.contains("zalo")) {
            if (contact != null && !contact.isBlank()) {
                return String.format("Bạn có thể liên hệ với chủ phòng: %s hoặc nhấn nút 'Gọi điện / Nhắn tin Zalo' trên trang.", contact);
            }
            return "Bạn có thể liên hệ chủ trọ bằng cách nhấn nút 'Nhắn tin' hoặc 'Gọi điện' ngay trên trang chi tiết phòng.";
        }

        // 6. Hỏi về đặt lịch xem phòng
        if (lower.contains("hẹn xem phòng") || lower.contains("đặt lịch") || lower.contains("xem phòng")) {
            return "Để hẹn xem phòng, bạn chỉ cần nhấn nút 'Đặt lịch xem phòng' ngay trên trang chi tiết phòng trọ. Chủ trọ sẽ nhận được thông báo và xác nhận lịch hẹn của bạn nhanh chóng.";
        }

        // 7. Chào hỏi
        if (lower.contains("xin chào") || lower.contains("hello") || lower.contains("chào")) {
            return "Xin chào bạn! Tôi là trợ lý AI thông minh của TroFinder. Tôi có thể giúp bạn tìm kiếm phòng trọ bằng ngôn ngữ tự nhiên, gợi ý phòng phù hợp và giải đáp thắc mắc về giá cả, tiện ích, hợp đồng thuê phòng.";
        }

        return "Cảm ơn bạn đã đặt câu hỏi. TroFinder luôn sẵn sàng hỗ trợ bạn tìm được căn phòng ưng ý với đầy đủ thông tin minh bạch và an toàn nhất.";
    }

    private Double extractArea(String context) {
        Pattern pattern = Pattern.compile("Diện tích:\\s*(\\d+(\\.\\d+)?)\\s*m2");
        Matcher matcher = pattern.matcher(context);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private String extractGroup(String context, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(context);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private String formatCurrency(String amount) {
        try {
            double val = Double.parseDouble(amount);
            return String.format("%,.0f", val).replace(",", ".");
        } catch (Exception e) {
            return amount;
        }
    }

    @Override
    public String getProviderName() {
        return "LOCAL_HEURISTIC_AI";
    }
}

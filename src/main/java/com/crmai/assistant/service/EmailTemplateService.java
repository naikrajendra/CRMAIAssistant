package com.crmai.assistant.service;

import com.crmai.assistant.model.DailyBriefing;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class EmailTemplateService {

    public String renderHtmlEmail(DailyBriefing briefing) {
        String bodyHtml = formatBodyHtml(briefing.body());

        return "<!DOCTYPE html>\n"
                + "<html lang=\"en\">\n"
                + "<head>\n"
                + "  <meta charset=\"UTF-8\">\n"
                + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "  <title>" + escapeHtml(briefing.subject()) + "</title>\n"
                + "</head>\n"
                + "<body style=\"margin:0; background:#f3f7fb; font-family:Arial, Helvetica, sans-serif; color:#10233d;\">\n"
                + "  <div style=\"max-width:700px; margin:32px auto; background:#ffffff; border:1px solid #dce6f2; border-radius:16px; overflow:hidden;\">\n"
                + "    <div style=\"background:linear-gradient(135deg,#0d5bd7,#173d7a); padding:24px 30px; color:#ffffff;\">\n"
                + "      <div style=\"font-size:12px; letter-spacing:1.8px; text-transform:uppercase; opacity:0.9;\">CRM Intelligence</div>\n"
                + "      <h1 style=\"margin:10px 0 0; font-size:24px; line-height:1.2;\">"
                + escapeHtml(briefing.accountName()) + " Daily Briefing</h1>\n"
                + "    </div>\n"
                + "    <div style=\"padding:28px 30px 18px;\">\n"
                + "      <div style=\"font-size:15px; line-height:1.7; color:#1f2d3d;\">\n"
                + bodyHtml +
                "      </div>\n"
                + "    </div>\n"
                + "    <div style=\"background:#0f1f3b; color:#dfe9ff; padding:18px 30px; font-size:12px;\">\n"
                + "      CRM Intelligence Assistant · Daily account brief\n"
                + "    </div>\n"
                + "  </div>\n"
                + "</body>\n"
                + "</html>\n";
    }

    private String formatBodyHtml(String body) {
        if (body == null || body.isBlank()) {
            return "<p>No account update was available for this briefing.</p>";
        }

        String[] paragraphs = body.split("\\n\\s*\\n|\\r?\\n");
        StringBuilder builder = new StringBuilder();
        for (String paragraph : paragraphs) {
            String cleaned = paragraph.trim();
            if (cleaned.isEmpty()) {
                continue;
            }
            builder.append("<p>")
                    .append(escapeHtml(cleaned).replace("\n", "<br/>"))
                    .append("</p>");
        }
        return builder.toString();
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}

package sn.ods.starterkit_spring.presentation.dto.responses.mails;

/**
 * @author G2k R&D
 */

public record MailInfosDTO(Long id, String  originalText, String subject, String text, String destinataire) {
}

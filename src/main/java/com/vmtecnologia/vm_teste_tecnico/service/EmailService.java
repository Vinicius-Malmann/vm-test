package com.vmtecnologia.vm_teste_tecnico.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailSenderService {

    /**
     * Envia um e-mail de forma simulada com tratamento de erros
     *
     * @param destinatario Email do destinatário
     * @param assunto Assunto do e-mail
     * @param corpo Corpo da mensagem
     * @return boolean indicando se o envio foi simulado com sucesso
     */
    public boolean sendEmail(String destinatario, String assunto, String corpo) {
        try {
            // Validação básica dos parâmetros
            if (destinatario == null || destinatario.isBlank()) {
                throw new IllegalArgumentException("Destinatário não pode ser vazio");
            }

            if (assunto == null || assunto.isBlank()) {
                throw new IllegalArgumentException("Assunto não pode ser vazio");
            }

            // Simula tempo de processamento (entre 0.5s e 2s)
            Thread.sleep(500 + (long) (Math.random() * 1500));

            // Simula falha aleatória em 10% dos casos (para testes)
            if (Math.random() < 0.1) {
                throw new RuntimeException("Falha simulada no envio de e-mail");
            }

//            // Registra o envio (simulado)
//            log.info("""
//                    ===== E-MAIL SIMULADO =====
//                    Para: {}
//                    Assunto: {}
//                    Corpo: {}
//                    ===========================
//                    """, destinatario, assunto, corpo);

            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrompida durante envio de e-mail para {}", destinatario, e);
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("Validação falhou para e-mail: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail para {}", destinatario, e);
            return false;
        }
    }

    /**
     * Versão simplificada para envio de e-mails de notificação
     *
     * @param destinatario Email do destinatário
     * @param nomeDestinatario Nome para personalização
     * @param tipoTemplate Tipo de template (cadastro, atualizacao, etc.)
     */
    public void enviarEmailNotificacao(String destinatario, String nomeDestinatario, String tipoTemplate) {
        String assunto = switch (tipoTemplate) {
            case "cadastro" -> "Cadastro realizado com sucesso";
            case "atualizacao" -> "Seus dados foram atualizados";
            default -> "Notificação do sistema";
        };

        String corpo = switch (tipoTemplate) {
            case "cadastro" -> String.format("""
                Olá %s,
                
                Bem-vindo ao nosso sistema! Seu cadastro foi realizado com sucesso.
                
                Atenciosamente,
                Equipe de Suporte
                """, nomeDestinatario);

            case "atualizacao" -> String.format("""
                Prezado %s,
                
                Informamos que seus dados foram atualizados em nosso sistema.
                
                Atenciosamente,
                Equipe de Suporte
                """, nomeDestinatario);

            default -> "Você recebeu uma notificação do sistema.";
        };

        enviarEmail(destinatario, assunto, corpo);
    }
}
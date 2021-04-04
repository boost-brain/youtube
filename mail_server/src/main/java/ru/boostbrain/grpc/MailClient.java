package ru.boostbrain.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;
import ru.boostbrain.Mail;
import ru.boostbrain.MailServerGrpc;

import java.util.List;

public class MailClient {
    public static void main(String[] args){
        //Создание клиентской заглушки
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        MailServerGrpc.MailServerBlockingStub stub = MailServerGrpc.newBlockingStub(channel);

        //Создаем объект письма
        Mail.Letter newLetter = Mail.Letter.newBuilder()
                .setAuthor("boostbrain")
                .setAddressee("helloworld")
                .setText("Текст письма")
                .build();

        //Создаем объект запроса на отправку почты, содержащего одно письмо
        Mail.SendMailRequest sendMailRequest = Mail.SendMailRequest.newBuilder()
                .addLetters(newLetter)
                .build();

        //Отправляем запрос на отправку почты
        Mail.SendMailResponse sendMailResponse = stub.sendMail(sendMailRequest);
        if(sendMailResponse.getSuccess()){
            System.out.println("Send mail success");
        }

        //Создаем объект запроса на получение почты
        Mail.GetMailRequest getMailRequest = Mail.GetMailRequest.newBuilder()
                .setAddressee("helloworld")
                .build();

        //Отправляем запрос на получение почты
        Mail.GetMailResponse getMailResponse = stub.getMail(getMailRequest);
        List<Mail.Letter> letters = getMailResponse.getLettersList();

        for (Mail.Letter letter : letters){
            System.out.println("From: " + newLetter.getAuthor());
            System.out.println("To: " + newLetter.getAddressee());
            System.out.println("Text: " + newLetter.getText());
            System.out.println("----------------------------");
        }
    }
}

package ru.boostbrain.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import ru.boostbrain.Mail;
import ru.boostbrain.MailServerGrpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MailServer extends MailServerGrpc.MailServerImplBase {
    private final Map<String, List<Mail.Letter>> letterMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder
                .forPort(8080)
                .addService(new MailServer())
                .build();

        server.start();
        server.awaitTermination();
    }

    @Override
    public void getMail(Mail.GetMailRequest request, StreamObserver<Mail.GetMailResponse> responseObserver) {
        String addressee = request.getAddressee();
        List<Mail.Letter> letters = letterMap.get(addressee);

        //Если писем еще нет - возвращаем пустой ответ
        if (letters == null) {
            Mail.GetMailResponse getMailResponse = Mail.GetMailResponse
                    .newBuilder()
                    .build();

            responseObserver.onNext(getMailResponse);
            responseObserver.onCompleted();
        }

        //Конструируем объект ответа и добавляем в него все найденные письма
        Mail.GetMailResponse getMailResponse = Mail.GetMailResponse
                .newBuilder()
                .addAllLetters(letters)
                .build();
        responseObserver.onNext(getMailResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void sendMail(Mail.SendMailRequest request, StreamObserver<Mail.SendMailResponse> responseObserver) {
        List<Mail.Letter> newLetters = request.getLettersList();

        //Перебираем все письма и добавляем их в списки
        for (Mail.Letter newLetter : newLetters) {
            String addressee = newLetter.getAddressee();
            List<Mail.Letter> letters = letterMap.
                    computeIfAbsent(addressee,
                            k -> Collections.synchronizedList(new ArrayList<>()));

            letters.add(newLetter);
        }

        //Возвращаем ответ об успешности
        Mail.SendMailResponse sendMailResponse = Mail.SendMailResponse
                .newBuilder()
                .setSuccess(true)
                .build();
        responseObserver.onNext(sendMailResponse);
        responseObserver.onCompleted();
    }
}

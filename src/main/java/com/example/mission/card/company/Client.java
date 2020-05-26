package com.example.mission.card.company;

import com.example.mission.payment.message.model.Message;
import com.example.mission.payment.message.service.MessageWriterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class Client {

    private CardComponeyRepository repository;

    private MessageWriterService messageWriter;

    public Client(CardComponeyRepository repository, MessageWriterService messageWriter) {
        this.repository = repository;
        this.messageWriter = messageWriter;
    }

    public CardCompanyResponse exchange(Message message) {

        RequestMessage requestMessage = new RequestMessage(this.messageWriter.writeAsString(message));
        repository.save(requestMessage);

        return new CardCompanyResponse(StringUtils.leftPad(String.valueOf(requestMessage.getId()), 20, '0'));
    }
}

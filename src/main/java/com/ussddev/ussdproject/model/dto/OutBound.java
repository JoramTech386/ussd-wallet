package com.ussddev.ussdproject.model.dto;

import com.ussddev.ussdproject.model.enums.ProcessState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutBound {

    private String message;

    private ProcessState command;


    public static OutBound continueWith(String message) {
        return OutBound.builder()
                .message(message)
                .command(ProcessState.CONTINUE)
                .build();
    }

    public static OutBound endWith(String message) {
        return OutBound.builder()
                .message(message)
                .command(ProcessState.END)
                .build();
    }
}

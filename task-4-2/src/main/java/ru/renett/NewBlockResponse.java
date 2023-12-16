package ru.renett;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewBlockResponse {
    private int status;
    private String statusString;
    private BlockModel block;
}

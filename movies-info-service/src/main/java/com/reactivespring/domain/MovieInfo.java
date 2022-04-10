package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class MovieInfo {

    @Id
    private String movieInfoId;

    @NotBlank(message = "movieInfo.name must be present")
    private String name;

    @NotNull
    @Positive(message = "movieInfo.year must be a Positive number")
    private Integer year;

    // By adding @NotBlank Over the type, we can make sure there would not be empty string inside the input list
    private List<@NotBlank(message = "movieInfo.cast should not be put empty string") String> cast;

    private LocalDate release_date;
}

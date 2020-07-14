package com.model;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "billionaires")
@Data
public class Billionaire {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;
    private String career;
}

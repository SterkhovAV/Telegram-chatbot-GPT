package io.sterkhovav.chatbotGPT.models;

import io.sterkhovav.chatbotGPT.enums.ModelGPTEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table( name = "gpt_users")
public class User {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "model_gpt", nullable = false)
    @Enumerated(EnumType.STRING)
    private ModelGPTEnum modelGPT;

    @Column(name = "amount_spent", nullable = false)
    private Double amountSpent;

    @Column(name = "active", nullable = false)
    private Boolean active;
}
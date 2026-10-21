package com.indra.retail.entities;

import jakarta.persistence.Embeddable;

@Embeddable
public record Address(String street, String city, String complement) {
}

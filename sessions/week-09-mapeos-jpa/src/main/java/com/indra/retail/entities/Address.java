package com.indra.retail.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Address(@Column String street,@Column String city,@Column String complement) {
}

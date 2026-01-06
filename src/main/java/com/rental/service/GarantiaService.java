package com.rental.service;

import com.rental.model.Garantia;

public class GarantiaService extends BaseService<Garantia> {

    public GarantiaService() {
        super("garantias", Garantia.class);
    }

    // Additional query methods can be added here if needed,
    // e.g., finding warranty by equipment ID.
}

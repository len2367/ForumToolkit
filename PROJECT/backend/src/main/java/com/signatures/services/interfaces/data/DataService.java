package com.signatures.services.interfaces.data;

import com.signatures.entities.AbstractAuditable;
import com.sun.istack.Nullable;

import java.util.Optional;

public interface DataService<ID, T extends AbstractAuditable> {

    @Nullable
    T getById(ID id);

    Optional<T> findById(ID id);

    T save(T object);

    void deleteById(ID id);
}

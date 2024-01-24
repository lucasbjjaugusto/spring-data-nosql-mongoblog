package com.fiap.springblog.service.impl;

import com.fiap.springblog.model.Autor;
import com.fiap.springblog.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutorServiceImpl implements com.fiap.springblog.service.AutorService {

    @Autowired
    private AutorRepository AutorRepository;

    @Override
    public List<Autor> obterTodos() {
        return AutorRepository.findAll();
    }

    @Override
    public Autor obterPorCodigo(String codigo) {
        return AutorRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException("Autor não existe"));
    }

    @Override
    public Autor criar(Autor Autor) {
        return AutorRepository.save(Autor);
    }
}

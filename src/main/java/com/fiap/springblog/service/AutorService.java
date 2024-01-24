package com.fiap.springblog.service;

import com.fiap.springblog.model.Autor;

import java.util.List;

public interface AutorService {
    public List<Autor> obterTodos();
    public Autor obterPorCodigo(String codigo);
    public Autor criar(Autor Autor);
}

package com.fiap.springblog.controller;

import com.fiap.springblog.model.Autor;
import com.fiap.springblog.service.AutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/autores")
public class AutorController {

    @Autowired
    private AutorService autorService;

    @GetMapping
    public List<Autor> obterTodos() {
        return autorService.obterTodos();
    }

    @GetMapping("/{codigo}")
    public Autor obterPorCodigo(@PathVariable String codigo) {
        return autorService.obterPorCodigo(codigo);
    }

    @PostMapping
    public Autor criar(@RequestBody Autor Autor) {
        return autorService.criar(Autor);
    }
}

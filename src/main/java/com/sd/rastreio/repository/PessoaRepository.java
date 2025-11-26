package com.sd.rastreio.repository;

import com.sd.rastreio.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {
    // JpaRepository<Entidade, TipoDoId>
}
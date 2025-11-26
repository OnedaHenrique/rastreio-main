package com.sd.rastreio.repository;

import com.sd.rastreio.model.Rastreio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // <--- Importe java.util.List
import java.util.Optional;

@Repository
public interface RastreioRepository extends JpaRepository<Rastreio, Integer> {
    
    Optional<Rastreio> findByCodigoRastreio(String codigoRastreio);

    List<Rastreio> findByPessoa_Id(Integer pessoaId);
}
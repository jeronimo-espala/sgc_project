package com.basis.sgcproject.service;

import com.basis.sgcproject.domain.*;
import com.basis.sgcproject.exception.EntidadeNaoEncontradaException;
import com.basis.sgcproject.exception.RegraNegocioException;
import com.basis.sgcproject.repository.ColaboradorRepository;
import com.basis.sgcproject.repository.CompetenciaRepository;
import com.basis.sgcproject.repository.TurmaFormacaoRepository;
import com.basis.sgcproject.service.dto.TurmaFormacaoDto;
import com.basis.sgcproject.service.mapper.TurmaFormacaoMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class TurmaFormacaoService {

    TurmaFormacaoRepository turmaFormacaoRepository;
    TurmaFormacaoMapper turmaFormacaoMapper;
    StatusService statusService;
    ColaboradorRepository colaboradorRepository;
    CompetenciaRepository competenciaRepository;

    public List<TurmaFormacaoDto> listarTodas() {
        return turmaFormacaoMapper.toDto(turmaFormacaoRepository.findAll());
    }

    public TurmaFormacaoDto buscarPeloId(Integer turmaFormacaoId) {
        return turmaFormacaoMapper.toDto(turmaFormacaoRepository.findById(turmaFormacaoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(String.format("Não existe uma turma com codigo %d", turmaFormacaoId))));
    }

    @Transactional
    public TurmaFormacaoDto salvar(TurmaFormacaoDto turmaFormacaoDto) {
        TurmaFormacao turma = turmaFormacaoMapper.toEntity(turmaFormacaoDto);
        Integer statusId = turma.getStatus().getId();
        Status status = statusService.buscarPeloId(statusId);
        turma.setStatus(status);
        turma.getCompetenciasColaboradores().forEach(item -> {
            Competencia competencia = competenciaRepository.getById(item.getId().getIdCompetencia());
            Colaborador colaborador = colaboradorRepository.getById(item.getId().getIdColaborador());
            item.setTurma(turma);
            item.setCompetencia(competencia);
            item.setColaborador(colaborador);
        });
        return turmaFormacaoMapper.toDto(turmaFormacaoRepository.save(turma));
    }

    @Transactional
    public TurmaFormacaoDto atualizar(Integer turmaFormacaoId, TurmaFormacaoDto turmaFormacaoDto) {
        TurmaFormacao turma = turmaFormacaoMapper.toEntity(turmaFormacaoDto);
        turma.setId(turmaFormacaoId);
        turma.getCompetenciasColaboradores().forEach(item -> {
            Competencia competencia = competenciaRepository.getById(item.getId().getIdCompetencia());
            Colaborador colaborador = colaboradorRepository.getById(item.getId().getIdColaborador());
            item.getId().setIdTurma(turma.getId());
            item.setTurma(turma);
            item.setCompetencia(competencia);
            item.setColaborador(colaborador);
        });
        TurmaFormacao turmaManaged = turmaFormacaoRepository.save(turma);
        return turmaFormacaoMapper.toDto(turmaManaged);
    }

    @Transactional
    public void excluir(Integer turmaFormacaoId) {
        turmaFormacaoRepository.deleteById(turmaFormacaoId);
    }
}
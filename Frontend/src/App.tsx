import { useCallback, useEffect, useMemo, useState } from 'react'
import { AlertCircle, CalendarDays, Check, CheckCircle2, ChevronLeft, CircleAlert, FileText, Filter, LoaderCircle, Search, Tag, Trash2, UserCircle, X, Zap } from 'lucide-react'
import './App.css'

type Prioridade = 'ALTA' | 'MEDIA' | 'BAIXA'
type Status = 'Aberto' | 'EmAndamento' | 'Resolvido' | 'Fechado'

type Responsavel = {
  id: string
  nome: string
  chamados?: Chamado[]
}

type Chamado = {
  id: string
  titulo: string
  descricao: string
  prioridade: Prioridade
  status: Status
  responsavel: Responsavel | null
  createdDate?: string | null
  lastModifiedDate?: string | null
}

type Page<T> = {
  content: T[]
}

type ApiError = {
  message?: string
}

const API_URL = import.meta.env.VITE_API_URL ?? '/api'

const prioridades: { value: Prioridade; label: string }[] = [
  { value: 'BAIXA', label: 'Baixa' },
  { value: 'MEDIA', label: 'Média' },
  { value: 'ALTA', label: 'Alta' },
]

const statuses: { value: Status; label: string }[] = [
  { value: 'Aberto', label: 'Aberto' },
  { value: 'EmAndamento', label: 'Em andamento' },
  { value: 'Resolvido', label: 'Resolvido' },
  { value: 'Fechado', label: 'Fechado' },
]

const initialForm = {
  titulo: '',
  descricao: '',
  prioridade: 'MEDIA' as Prioridade,
  status: 'Aberto' as Status,
  responsavelId: '',
}

const initialFilters = {
  busca: '',
  status: '',
  prioridade: '',
  responsavelId: '',
}

type EditForm = typeof initialForm & {
  id: string
  createdDate?: string | null
}

const prioridadeLabel = (value: Prioridade) =>
  prioridades.find(prioridade => prioridade.value === value)?.label ?? value

const statusLabel = (value: Status) =>
  statuses.find(status => status.value === value)?.label ?? value

const formatarData = (value?: string | null) => {
  if (!value) return 'Não informado'

  return new Intl.DateTimeFormat('pt-BR', {
    dateStyle: 'short',
    timeStyle: 'short',
  }).format(new Date(value))
}

async function apiRequest<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
    ...options,
  })

  if (!response.ok) {
    const body = await response.json().catch(() => null) as ApiError | null
    throw new Error(body?.message || 'Não foi possível concluir a operação.')
  }

  const contentType = response.headers.get('content-type')
  if (contentType?.includes('application/json')) {
    return response.json() as Promise<T>
  }

  return response.text() as Promise<T>
}

function App() {
  const [aba, setAba] = useState<'novo' | 'lista'>('novo')
  const [etapa, setEtapa] = useState(1)
  const [chamados, setChamados] = useState<Chamado[]>([])
  const [responsaveis, setResponsaveis] = useState<Responsavel[]>([])
  const [form, setForm] = useState(initialForm)
  const [carregando, setCarregando] = useState(true)
  const [salvando, setSalvando] = useState(false)
  const [atribuindo, setAtribuindo] = useState(false)
  const [erro, setErro] = useState('')
  const [sucesso, setSucesso] = useState('')
  const [filtros, setFiltros] = useState(initialFilters)
  const [chamadoDetalhado, setChamadoDetalhado] = useState<Chamado | null>(null)
  const [detalheCarregando, setDetalheCarregando] = useState(false)
  const [detalheErro, setDetalheErro] = useState('')
  const [confirmandoExclusao, setConfirmandoExclusao] = useState(false)
  const [excluindo, setExcluindo] = useState(false)
  const [edicao, setEdicao] = useState<EditForm | null>(null)
  const [edicaoCarregando, setEdicaoCarregando] = useState(false)
  const [edicaoErro, setEdicaoErro] = useState('')
  const [atualizando, setAtualizando] = useState(false)
  const [chamadoParaConcluir, setChamadoParaConcluir] = useState<Chamado | null>(null)
  const [concluindo, setConcluindo] = useState(false)
  const [conclusaoErro, setConclusaoErro] = useState('')

  const chamadosFiltrados = useMemo(() => {
    const busca = filtros.busca.trim().toLocaleLowerCase('pt-BR')

    return chamados.filter(chamado => {
      const correspondeBusca = !busca
        || chamado.titulo.toLocaleLowerCase('pt-BR').includes(busca)
        || chamado.descricao.toLocaleLowerCase('pt-BR').includes(busca)
      const correspondeStatus = !filtros.status || chamado.status === filtros.status
      const correspondePrioridade = !filtros.prioridade || chamado.prioridade === filtros.prioridade
      const correspondeResponsavel = !filtros.responsavelId || chamado.responsavel?.id === filtros.responsavelId

      return correspondeBusca && correspondeStatus && correspondePrioridade && correspondeResponsavel
    })
  }, [chamados, filtros])

  const carregarDados = useCallback(async () => {
    setCarregando(true)
    setErro('')

    try {
      const [paginaResponsaveis, paginaChamados] = await Promise.all([
        apiRequest<Page<Responsavel>>('/responsaveis?page=0&size=100&sortBy=nome&direction=asc'),
        apiRequest<Page<Chamado>>('/chamados?page=0&size=100&sortBy=createdDate&direction=desc'),
      ])
      setResponsaveis(paginaResponsaveis.content)
      setChamados(paginaChamados.content)
    } catch (error) {
      setErro(error instanceof Error ? error.message : 'Não foi possível carregar os dados.')
    } finally {
      setCarregando(false)
    }
  }, [])

  useEffect(() => {
    const timeoutId = window.setTimeout(() => void carregarDados(), 0)
    return () => window.clearTimeout(timeoutId)
  }, [carregarDados])

  useEffect(() => {
    if (!chamadoDetalhado) return

    const fecharComEsc = (evento: KeyboardEvent) => {
      if (evento.key === 'Escape' && !excluindo) setChamadoDetalhado(null)
    }

    document.body.style.overflow = 'hidden'
    window.addEventListener('keydown', fecharComEsc)

    return () => {
      document.body.style.overflow = ''
      window.removeEventListener('keydown', fecharComEsc)
    }
  }, [chamadoDetalhado, excluindo])

  useEffect(() => {
    if (!edicao) return

    const fecharComEsc = (evento: KeyboardEvent) => {
      if (evento.key === 'Escape' && !atualizando) setEdicao(null)
    }

    document.body.style.overflow = 'hidden'
    window.addEventListener('keydown', fecharComEsc)

    return () => {
      document.body.style.overflow = ''
      window.removeEventListener('keydown', fecharComEsc)
    }
  }, [atualizando, edicao])

  useEffect(() => {
    if (!chamadoParaConcluir) return

    const fecharComEsc = (evento: KeyboardEvent) => {
      if (evento.key === 'Escape' && !concluindo) setChamadoParaConcluir(null)
    }

    document.body.style.overflow = 'hidden'
    window.addEventListener('keydown', fecharComEsc)

    return () => {
      document.body.style.overflow = ''
      window.removeEventListener('keydown', fecharComEsc)
    }
  }, [chamadoParaConcluir, concluindo])

  const alterar = <Campo extends keyof typeof form>(campo: Campo, valor: (typeof form)[Campo]) => {
    setForm(atual => ({ ...atual, [campo]: valor }))
    setErro('')
    setSucesso('')
  }

  const avancar = () => {
    if (!form.titulo.trim() || !form.descricao.trim()) {
      setErro('Preencha o título e a descrição do chamado.')
      return
    }
    setErro('')
    setEtapa(2)
  }

  const cadastrar = async () => {
    if (!form.responsavelId) {
      setErro('Selecione um responsável.')
      return
    }

    setSalvando(true)
    setErro('')
    setSucesso('')

    try {
      await apiRequest<Chamado>('/chamados', {
        method: 'POST',
        body: JSON.stringify({
          titulo: form.titulo.trim(),
          descricao: form.descricao.trim(),
          prioridade: form.prioridade,
          status: form.status,
          responsavel: { id: form.responsavelId },
        }),
      })
      setForm(initialForm)
      setEtapa(1)
      setAba('lista')
      setSucesso('Chamado cadastrado com sucesso.')
      await carregarDados()
    } catch (error) {
      setErro(error instanceof Error ? error.message : 'Não foi possível cadastrar o chamado.')
    } finally {
      setSalvando(false)
    }
  }

  const atribuirAutomaticamente = async () => {
    setAtribuindo(true)
    setErro('')

    try {
      const responsavelId = await apiRequest<string>('/responsaveis/atribuir')
      setForm(atual => ({ ...atual, responsavelId }))
    } catch (error) {
      setErro(error instanceof Error ? error.message : 'Não foi possível atribuir um responsável.')
    } finally {
      setAtribuindo(false)
    }
  }

  const abrirNovoChamado = () => {
    setAba('novo')
    setEtapa(1)
    setErro('')
    setSucesso('')
  }

  const preencherEdicao = (chamado: Chamado): EditForm => ({
    id: chamado.id,
    titulo: chamado.titulo,
    descricao: chamado.descricao,
    prioridade: chamado.prioridade,
    status: chamado.status,
    responsavelId: chamado.responsavel?.id ?? '',
    createdDate: chamado.createdDate,
  })

  const abrirEdicao = async (chamado: Chamado) => {
    setEdicao(preencherEdicao(chamado))
    setEdicaoCarregando(true)
    setEdicaoErro('')
    setErro('')
    setSucesso('')

    try {
      const [detalhe, paginaResponsaveis] = await Promise.all([
        apiRequest<Chamado>(`/chamados/${chamado.id}`),
        apiRequest<Page<Responsavel>>('/responsaveis?page=0&size=100&sortBy=nome&direction=asc'),
      ])
      setEdicao(atual => atual?.id === chamado.id ? preencherEdicao(detalhe) : atual)
      setResponsaveis(paginaResponsaveis.content)
    } catch (error) {
      setEdicaoErro(error instanceof Error ? error.message : 'Não foi possível carregar os dados para edição.')
    } finally {
      setEdicaoCarregando(false)
    }
  }

  const fecharEdicao = () => {
    if (atualizando) return
    setEdicao(null)
    setEdicaoErro('')
  }

  const abrirDetalhes = async (chamado: Chamado) => {
    setChamadoDetalhado(chamado)
    setDetalheCarregando(true)
    setDetalheErro('')

    try {
      const detalhe = await apiRequest<Chamado>(`/chamados/${chamado.id}`)
      setChamadoDetalhado(atual => atual?.id === chamado.id ? detalhe : atual)
    } catch (error) {
      setDetalheErro(error instanceof Error ? error.message : 'Não foi possível carregar os detalhes do chamado.')
    } finally {
      setDetalheCarregando(false)
    }
  }

  const fecharDetalhes = () => {
    if (excluindo) return
    setChamadoDetalhado(null)
    setDetalheErro('')
    setConfirmandoExclusao(false)
  }

  const excluirChamado = async () => {
    if (!chamadoDetalhado) return

    setExcluindo(true)
    setDetalheErro('')

    try {
      await apiRequest<string>(`/chamados/${chamadoDetalhado.id}`, { method: 'DELETE' })
      setChamados(atuais => atuais.filter(chamado => chamado.id !== chamadoDetalhado.id))
      setChamadoDetalhado(null)
      setConfirmandoExclusao(false)
      setSucesso(`Chamado "${chamadoDetalhado.titulo}" excluído com sucesso.`)
      await carregarDados()
    } catch (error) {
      setDetalheErro(error instanceof Error ? error.message : 'Não foi possível excluir o chamado.')
      setConfirmandoExclusao(false)
    } finally {
      setExcluindo(false)
    }
  }

  const atualizarEdicao = <Campo extends keyof EditForm>(campo: Campo, valor: EditForm[Campo]) => {
    setEdicao(atual => atual ? { ...atual, [campo]: valor } : atual)
    setEdicaoErro('')
  }

  const salvarEdicao = async () => {
    if (!edicao || !edicao.titulo.trim() || !edicao.descricao.trim() || !edicao.responsavelId) {
      setEdicaoErro('Preencha todos os campos para atualizar o chamado.')
      return
    }

    setAtualizando(true)
    setEdicaoErro('')

    try {
      const atualizado = await apiRequest<Chamado>(`/chamados/${edicao.id}`, {
        method: 'PUT',
        body: JSON.stringify({
          titulo: edicao.titulo.trim(),
          descricao: edicao.descricao.trim(),
          prioridade: edicao.prioridade,
          status: edicao.status,
          responsavel: { id: edicao.responsavelId },
        }),
      })
      const confirmado = await apiRequest<Chamado>(`/chamados/${edicao.id}`)
      setChamados(atuais => atuais.map(chamado => chamado.id === confirmado.id ? confirmado : chamado))
      setEdicao(null)
      setSucesso(`Chamado "${atualizado.titulo}" atualizado com sucesso.`)
      await carregarDados()
    } catch (error) {
      setEdicaoErro(error instanceof Error ? error.message : 'Não foi possível atualizar o chamado.')
    } finally {
      setAtualizando(false)
    }
  }

  const abrirConclusao = (chamado: Chamado) => {
    setChamadoParaConcluir(chamado)
    setConclusaoErro('')
    setSucesso('')
  }

  const fecharConclusao = () => {
    if (concluindo) return
    setChamadoParaConcluir(null)
    setConclusaoErro('')
  }

  const concluirChamado = async () => {
    if (!chamadoParaConcluir) return

    setConcluindo(true)
    setConclusaoErro('')

    try {
      const atualizado = await apiRequest<Chamado>(`/chamados/${chamadoParaConcluir.id}`, {
        method: 'PUT',
        body: JSON.stringify({
          titulo: chamadoParaConcluir.titulo,
          descricao: chamadoParaConcluir.descricao,
          prioridade: chamadoParaConcluir.prioridade,
          status: 'Resolvido',
          responsavel: chamadoParaConcluir.responsavel
            ? { id: chamadoParaConcluir.responsavel.id }
            : null,
        }),
      })
      const confirmado = await apiRequest<Chamado>(`/chamados/${chamadoParaConcluir.id}`)
      setChamados(atuais => atuais.map(chamado => chamado.id === confirmado.id ? confirmado : chamado))
      setChamadoParaConcluir(null)
      setSucesso(`Chamado "${atualizado.titulo}" concluído com sucesso.`)
      await carregarDados()
    } catch (error) {
      setConclusaoErro(error instanceof Error ? error.message : 'Não foi possível concluir o chamado.')
    } finally {
      setConcluindo(false)
    }
  }

  return (
    <div className="app">
      <header className="top-header">
        <div className="container">
          <h1>Sistema de Chamados</h1>
        </div>
      </header>

      <nav className="tabs-bar">
        <div className="container tabs">
          <button className={aba === 'novo' ? 'active' : ''} onClick={abrirNovoChamado}>
            Novo Chamado
          </button>
          <button className={aba === 'lista' ? 'active' : ''} onClick={() => setAba('lista')}>
            Visualizar Chamados ({chamados.length})
          </button>
        </div>
      </nav>

      <main className="page">
        <div className="container">
          {erro && <div className="feedback error" role="alert"><AlertCircle />{erro}</div>}
          {sucesso && <div className="feedback success" role="status"><CheckCircle2 />{sucesso}</div>}

          {aba === 'novo' ? (
            <section className="card">
              <div className="steps" aria-label="Etapas do cadastro">
                <button className={etapa === 1 ? 'step active' : 'step done'} onClick={() => setEtapa(1)}>
                  <span>1</span>
                  <strong>Dados do Chamado</strong>
                </button>
                <span className="chevron">›</span>
                <button className={etapa === 2 ? 'step active' : 'step'} onClick={avancar}>
                  <span>2</span>
                  <strong>Selecionar Responsável</strong>
                </button>
              </div>

              {etapa === 1 ? (
                <div key="dados-chamado" className="form-content step-panel slide-from-left">
                  <h2>Cadastrar Novo Chamado</h2>

                  <label className="field">
                    <span>Título *</span>
                    <input
                      value={form.titulo}
                      onChange={evento => alterar('titulo', evento.target.value)}
                      placeholder="Descreva brevemente o problema"
                    />
                  </label>

                  <label className="field">
                    <span>Descrição *</span>
                    <textarea
                      value={form.descricao}
                      onChange={evento => alterar('descricao', evento.target.value)}
                      placeholder="Forneça mais detalhes sobre o chamado"
                    />
                  </label>

                  <div className="field-row">
                    <label className="field">
                      <span>Prioridade *</span>
                      <select value={form.prioridade} onChange={evento => alterar('prioridade', evento.target.value as Prioridade)}>
                        {prioridades.map(prioridade => <option key={prioridade.value} value={prioridade.value}>{prioridade.label}</option>)}
                      </select>
                    </label>
                    <label className="field">
                      <span>Status *</span>
                      <select value={form.status} onChange={evento => alterar('status', evento.target.value as Status)}>
                        {statuses.map(status => <option key={status.value} value={status.value}>{status.label}</option>)}
                      </select>
                    </label>
                  </div>

                  <div className="card-actions">
                    <button className="primary-button" onClick={avancar}>
                      Avançar <span>›</span>
                    </button>
                  </div>
                </div>
              ) : (
                <div key="selecionar-responsavel" className="form-content second-step step-panel slide-from-right">
                  <div className="responsaveis-heading">
                    <div>
                      <h2>Selecionar Responsável</h2>
                      <p className="helper">Escolha um responsável para este chamado</p>
                    </div>
                    <button
                      className="automatic-button"
                      onClick={() => void atribuirAutomaticamente()}
                      disabled={atribuindo || responsaveis.length === 0}
                    >
                      {atribuindo ? <LoaderCircle className="spin" /> : <Zap />}
                      {atribuindo ? 'Atribuindo...' : 'Atribuir Automaticamente'}
                    </button>
                  </div>

                  {carregando ? (
                    <div className="loading-state"><LoaderCircle className="spin" />Carregando responsáveis...</div>
                  ) : responsaveis.length === 0 ? (
                    <div className="empty-state compact">
                      <UserCircle />
                      <h3>Nenhum responsável cadastrado</h3>
                      <p>Cadastre um responsável no sistema antes de criar um chamado.</p>
                    </div>
                  ) : (
                    <div className="responsaveis">
                      {responsaveis.map(responsavel => (
                        <label className={`responsavel ${form.responsavelId === responsavel.id ? 'selected' : ''}`} key={responsavel.id}>
                          <input
                            type="radio"
                            name="responsavel"
                            checked={form.responsavelId === responsavel.id}
                            onChange={() => alterar('responsavelId', responsavel.id)}
                          />
                          <span className="radio-mark" />
                          <span className="responsavel-info">
                            <strong><UserCircle />{responsavel.nome}</strong>
                            <span className="responsavel-tags">
                              <b>{responsavel.chamados?.length ?? 0} chamados vinculados</b>
                            </span>
                          </span>
                        </label>
                      ))}
                    </div>
                  )}

                  <div className="card-actions split">
                    <button className="secondary-button back-button" onClick={() => setEtapa(1)}><ChevronLeft />Voltar</button>
                    <button
                      className="primary-button"
                      onClick={() => void cadastrar()}
                      disabled={salvando || responsaveis.length === 0}
                    >
                      {salvando && <LoaderCircle className="spin" />}
                      {salvando ? 'Cadastrando...' : 'Cadastrar Chamado'}
                    </button>
                  </div>
                </div>
              )}
            </section>
          ) : (
            <div className="chamados-view">
              <section className="card filters-card">
                <h2><Filter />Filtros</h2>
                <div className="filters-grid">
                  <label className="filter-field">
                    <span>Buscar</span>
                    <span className="search-input">
                      <Search />
                      <input
                        value={filtros.busca}
                        onChange={evento => setFiltros(atual => ({ ...atual, busca: evento.target.value }))}
                        placeholder="Título ou descrição..."
                      />
                    </span>
                  </label>
                  <label className="filter-field">
                    <span>Status</span>
                    <select value={filtros.status} onChange={evento => setFiltros(atual => ({ ...atual, status: evento.target.value }))}>
                      <option value="">Todos</option>
                      {statuses.map(status => <option key={status.value} value={status.value}>{status.label}</option>)}
                    </select>
                  </label>
                  <label className="filter-field">
                    <span>Prioridade</span>
                    <select value={filtros.prioridade} onChange={evento => setFiltros(atual => ({ ...atual, prioridade: evento.target.value }))}>
                      <option value="">Todas</option>
                      {prioridades.map(prioridade => <option key={prioridade.value} value={prioridade.value}>{prioridade.label}</option>)}
                    </select>
                  </label>
                  <label className="filter-field">
                    <span>Responsável</span>
                    <select value={filtros.responsavelId} onChange={evento => setFiltros(atual => ({ ...atual, responsavelId: evento.target.value }))}>
                      <option value="">Todos</option>
                      {responsaveis.map(responsavel => <option key={responsavel.id} value={responsavel.id}>{responsavel.nome}</option>)}
                    </select>
                  </label>
                </div>
              </section>

              <section className="card chamados-card">
                <div className="tickets-heading">
                  <h2>Chamados ({chamadosFiltrados.length})</h2>
                  {Object.values(filtros).some(Boolean) && (
                    <button className="clear-filters" onClick={() => setFiltros(initialFilters)}>Limpar filtros</button>
                  )}
                </div>

                {carregando ? (
                  <div className="loading-state"><LoaderCircle className="spin" />Carregando chamados...</div>
                ) : chamados.length === 0 ? (
                  <div className="empty-state">
                    <span>0</span>
                    <h3>Nenhum chamado cadastrado</h3>
                    <p>Cadastre um novo chamado para começar.</p>
                  </div>
                ) : chamadosFiltrados.length === 0 ? (
                  <div className="empty-state">
                    <Search />
                    <h3>Nenhum chamado encontrado</h3>
                    <p>Altere ou limpe os filtros para ver outros resultados.</p>
                  </div>
                ) : (
                  <div className="tickets">
                    {chamadosFiltrados.map(chamado => (
                      <article className="ticket" key={chamado.id}>
                        <div className="ticket-content">
                          <h3>{chamado.titulo}</h3>
                          <p>{chamado.descricao}</p>
                          <div className="ticket-tags">
                            <span className={`status-tag ${chamado.status.toLowerCase()}`}>{statusLabel(chamado.status)}</span>
                            <span className={`priority ${chamado.prioridade.toLowerCase()}`}>{prioridadeLabel(chamado.prioridade)}</span>
                            <span className="owner-tag">{chamado.responsavel?.nome ?? 'Não atribuído'}</span>
                          </div>
                        </div>
                        <div className="ticket-actions">
                          {(chamado.status === 'Aberto' || chamado.status === 'EmAndamento') && (
                            <button className="complete-button" onClick={() => abrirConclusao(chamado)}><Check />Concluir</button>
                          )}
                          <button className="secondary-button" onClick={() => void abrirDetalhes(chamado)}>Ver Detalhes</button>
                          <button className="primary-button" onClick={() => void abrirEdicao(chamado)}>Editar</button>
                        </div>
                      </article>
                    ))}
                  </div>
                )}
              </section>
            </div>
          )}
        </div>
      </main>

      {chamadoDetalhado && (
        <div className="modal-backdrop" role="presentation" onMouseDown={fecharDetalhes}>
          <section className="modal-card details-modal" role="dialog" aria-modal="true" aria-labelledby="details-title" onMouseDown={evento => evento.stopPropagation()}>
            <div className="details-modal-heading">
              <h2 id="details-title">Detalhes do Chamado</h2>
              <button className="details-close-button" aria-label="Fechar detalhes" onClick={fecharDetalhes}><X /></button>
            </div>

            <div className="details-modal-body">
              {detalheCarregando && (
                <div className="details-loading"><LoaderCircle className="spin" />Atualizando detalhes...</div>
              )}
              {detalheErro && <div className="feedback error details-feedback" role="alert"><AlertCircle />{detalheErro}</div>}

              <h3>{chamadoDetalhado.titulo}</h3>
              <div className="details-tags">
                <span className={`status-tag ${chamadoDetalhado.status.toLowerCase()}`}>{statusLabel(chamadoDetalhado.status)}</span>
                <span className={`priority ${chamadoDetalhado.prioridade.toLowerCase()}`}>Prioridade: {prioridadeLabel(chamadoDetalhado.prioridade)}</span>
              </div>

              <dl className="details-list">
                <div>
                  <FileText />
                  <span><dt>Descrição</dt><dd>{chamadoDetalhado.descricao}</dd></span>
                </div>
                <div>
                  <Tag />
                  <span><dt>Responsável</dt><dd><b className="owner-tag">{chamadoDetalhado.responsavel?.nome ?? 'Não atribuído'}</b></dd></span>
                </div>
                <div>
                  <CircleAlert />
                  <span><dt>ID do Chamado</dt><dd className="details-id">{chamadoDetalhado.id}</dd></span>
                </div>
                <div>
                  <CalendarDays />
                  <span>
                    <dt>Datas</dt>
                    <dd>Criado em: {formatarData(chamadoDetalhado.createdDate)}</dd>
                    <dd>Atualizado em: {formatarData(chamadoDetalhado.lastModifiedDate)}</dd>
                  </span>
                </div>
              </dl>

              {confirmandoExclusao && (
                <div className="delete-confirmation" role="alertdialog" aria-label="Confirmar exclusão">
                  <div>
                    <Trash2 />
                    <span>
                      <strong>Excluir este chamado?</strong>
                      <small>Esta ação é permanente e não poderá ser desfeita.</small>
                    </span>
                  </div>
                  <div className="delete-confirmation-actions">
                    <button className="secondary-button" disabled={excluindo} onClick={() => setConfirmandoExclusao(false)}>Cancelar</button>
                    <button className="danger-button" disabled={excluindo} onClick={() => void excluirChamado()}>
                      {excluindo ? <LoaderCircle className="spin" /> : <Trash2 />}
                      {excluindo ? 'Excluindo...' : 'Confirmar Exclusão'}
                    </button>
                  </div>
                </div>
              )}
            </div>

            <div className="modal-actions details-modal-actions">
              <button
                className="danger-link-button"
                disabled={excluindo || chamadoDetalhado.status !== 'Fechado'}
                title={chamadoDetalhado.status !== 'Fechado' ? 'Somente chamados fechados podem ser excluídos' : undefined}
                onClick={() => setConfirmandoExclusao(true)}
              >
                <Trash2 />Excluir Chamado
              </button>
              <span className="details-actions-spacer" />
              <button className="secondary-button details-secondary-button" disabled={excluindo} onClick={fecharDetalhes}>Fechar</button>
              <button className="primary-button" disabled={excluindo} onClick={() => { fecharDetalhes(); void abrirEdicao(chamadoDetalhado) }}>Editar Chamado</button>
            </div>
          </section>
        </div>
      )}

      {edicao && (
        <div className="modal-backdrop" role="presentation" onMouseDown={fecharEdicao}>
          <section className="modal-card edit-modal" role="dialog" aria-modal="true" aria-labelledby="edit-title" onMouseDown={evento => evento.stopPropagation()}>
            <div className="edit-modal-heading">
              <h2 id="edit-title">Editar Chamado</h2>
              <button className="details-close-button" aria-label="Fechar edição" disabled={atualizando} onClick={fecharEdicao}><X /></button>
            </div>

            <div className="edit-modal-body">
              {edicaoCarregando && <div className="details-loading"><LoaderCircle className="spin" />Atualizando dados do chamado...</div>}
              {edicaoErro && <div className="feedback error edit-feedback" role="alert"><AlertCircle />{edicaoErro}</div>}

              <label className="edit-field">
                <span>Título *</span>
                <input disabled={edicaoCarregando || atualizando} value={edicao.titulo} onChange={evento => atualizarEdicao('titulo', evento.target.value)} />
              </label>
              <label className="edit-field">
                <span>Descrição *</span>
                <textarea disabled={edicaoCarregando || atualizando} value={edicao.descricao} onChange={evento => atualizarEdicao('descricao', evento.target.value)} />
              </label>
              <label className="edit-field">
                <span>Responsável *</span>
                <select disabled={edicaoCarregando || atualizando} value={edicao.responsavelId} onChange={evento => atualizarEdicao('responsavelId', evento.target.value)}>
                  <option value="">Selecione</option>
                  {responsaveis.map(responsavel => <option key={responsavel.id} value={responsavel.id}>{responsavel.nome}</option>)}
                </select>
              </label>
              <div className="edit-field-row">
                <label className="edit-field">
                  <span>Prioridade *</span>
                  <select disabled={edicaoCarregando || atualizando} value={edicao.prioridade} onChange={evento => atualizarEdicao('prioridade', evento.target.value as Prioridade)}>
                    {prioridades.map(prioridade => <option key={prioridade.value} value={prioridade.value}>{prioridade.label}</option>)}
                  </select>
                </label>
                <label className="edit-field">
                  <span>Status *</span>
                  <select disabled={edicaoCarregando || atualizando} value={edicao.status} onChange={evento => atualizarEdicao('status', evento.target.value as Status)}>
                    {statuses.map(status => <option key={status.value} value={status.value}>{status.label}</option>)}
                  </select>
                </label>
              </div>

              <div className="edit-metadata">
                <p><strong>ID do Chamado:</strong> {edicao.id}</p>
                <p><strong>Criado em:</strong> {formatarData(edicao.createdDate)}</p>
              </div>
            </div>

            <div className="modal-actions edit-modal-actions">
              <button className="secondary-button details-secondary-button" disabled={atualizando} onClick={fecharEdicao}>Cancelar</button>
              <button className="primary-button" disabled={edicaoCarregando || atualizando} onClick={() => void salvarEdicao()}>
                {atualizando && <LoaderCircle className="spin" />}
                {atualizando ? 'Salvando...' : 'Salvar Alterações'}
              </button>
            </div>
          </section>
        </div>
      )}

      {chamadoParaConcluir && (
        <div className="modal-backdrop" role="presentation" onMouseDown={fecharConclusao}>
          <section className="modal-card conclusion-modal" role="dialog" aria-modal="true" aria-labelledby="conclusion-title" onMouseDown={evento => evento.stopPropagation()}>
            <div className="conclusion-icon"><CheckCircle2 /></div>
            <h2 id="conclusion-title">Concluir chamado?</h2>
            <p>
              O chamado <strong>{chamadoParaConcluir.titulo}</strong> será marcado como
              <b> Resolvido</b> e permanecerá registrado no sistema.
            </p>
            {conclusaoErro && <div className="feedback error conclusion-feedback" role="alert"><AlertCircle />{conclusaoErro}</div>}
            <div className="conclusion-actions">
              <button className="secondary-button" disabled={concluindo} onClick={fecharConclusao}>Cancelar</button>
              <button className="complete-confirm-button" disabled={concluindo} onClick={() => void concluirChamado()}>
                {concluindo ? <LoaderCircle className="spin" /> : <Check />}
                {concluindo ? 'Concluindo...' : 'Concluir Chamado'}
              </button>
            </div>
          </section>
        </div>
      )}
    </div>
  )
}

export default App

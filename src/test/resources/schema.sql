CREATE TABLE IF NOT EXISTS modular_tiposusuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    idEmpresa BIGINT,
    dataOcorrenciaCriacao TIMESTAMP,
    nomeUsuarioCriacao VARCHAR(255),
    dataOcorrenciaAlteracao TIMESTAMP,
    nomeUsuarioAlteracao VARCHAR(255)
);

CREATE TABLE modular_telas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(45) NOT NULL,
    query CHAR(1) CHECK (query IN ('Y', 'N', 'T', 'F', 'true', 'false')) -- Compatível com o TrueFalseConverter do Hibernate
);

ALTER TABLE modular_telas ADD CONSTRAINT UK_modular_telas_nome UNIQUE (nome);

CREATE INDEX IDX_modular_telas_nome ON modular_telas (nome);

CREATE TABLE modular_permissoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    acao VARCHAR(30) NOT NULL,
    idTela BIGINT NOT NULL,
    CONSTRAINT FK_modular_permissoes_tela FOREIGN KEY (idTela) REFERENCES modular_telas(id)
);

ALTER TABLE modular_permissoes ADD CONSTRAINT UK_modular_permissoes_acao_idTela UNIQUE (acao, idTela);

CREATE INDEX IDX_modular_permissoes_acao ON modular_permissoes (acao);
CREATE INDEX IDX_modular_permissoes_idTela ON modular_permissoes (idTela);


CREATE TABLE modular_usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(18) NOT NULL,
    senha VARCHAR(50) NOT NULL,
    senhaTemporaria VARCHAR(60),
    email VARCHAR(100) NOT NULL,
    status CHAR(1) NOT NULL CHECK (status IN ('Y', 'N', 'T', 'F', 'true', 'false')),
    idTipoUsuario BIGINT NOT NULL,
    idEmpresa BIGINT,
    dataOcorrenciaCriacao TIMESTAMP,
    nomeUsuarioCriacao VARCHAR(255),
    dataOcorrenciaAlteracao TIMESTAMP,
    nomeUsuarioAlteracao VARCHAR(255),
    CONSTRAINT FK_modular_usuarios_tipousuario FOREIGN KEY (idTipoUsuario) REFERENCES modular_tiposusuario(id)
);

ALTER TABLE modular_usuarios ADD CONSTRAINT UK_modular_usuarios_login_senha UNIQUE (login, senha);
ALTER TABLE modular_usuarios ADD CONSTRAINT UK_modular_usuarios_login_idEmpresa UNIQUE (login, idEmpresa);

CREATE INDEX IDX_modular_usuarios_login ON modular_usuarios (login);
CREATE INDEX IDX_modular_usuarios_idTipoUsuario ON modular_usuarios (idTipoUsuario);

-- Tabela de relacionamento ManyToMany (Usuario <-> Permissao)
CREATE TABLE modular_permissoesusuario (
    idUsuario BIGINT NOT NULL,
    idPermissao BIGINT NOT NULL,
    CONSTRAINT PK_modular_permissoesusuario PRIMARY KEY (idUsuario, idPermissao),
    CONSTRAINT FK_modular_permissoesusuario_usuario FOREIGN KEY (idUsuario) REFERENCES modular_usuarios(id),
    CONSTRAINT FK_modular_permissoesusuario_permissao FOREIGN KEY (idPermissao) REFERENCES modular_permissoes(id)
);
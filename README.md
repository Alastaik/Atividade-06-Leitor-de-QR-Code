# Atividade 06 — Leitor de QR Code

Aplicativo Android da atividade **Leitor de QR Code com CameraX e ML Kit**.
Projeto `LeitorQRCode`, pacote `com.aula.leitorqrcode`, Kotlin e Android 7.0 ou superior (minSdk 24).

## Executar

1. Abra esta pasta no Android Studio e sincronize o Gradle.
2. Use JDK 17 ou 21 e instale o SDK Android 35.
3. Conecte um celular ou inicie um emulador com câmera.
4. Execute o módulo `app` e conceda a permissão de câmera.
5. Aponte para os códigos da folha fornecida pelo professor.

O ML Kit está incluído no APK. A leitura funciona sem internet.

## Funcionalidades

- Permissão de câmera, justificativa e acesso às configurações quando bloqueada.
- Pré-visualização de 260 dp com CameraX.
- Leitura exclusiva de QR codes com `MlKitAnalyzer`.
- Link, texto, Wi-Fi, telefone, e-mail e localização.
- Wi-Fi mostra apenas o nome da rede.
- Histórico com horário, sem repetições, mais recente primeiro.
- Histórico preservado durante a rotação da tela.
- Botão Limpar e botão Abrir quando há ação disponível.
- Aviso quando nenhum aplicativo consegue abrir o conteúdo.

O histórico fica em memória no ViewModel. Encerrar o processo apaga as leituras.
Após limpar, um código ainda diante da câmera pode ser registrado novamente.
Nenhuma imagem da câmera é salva. O valor bruto do Wi-Fi fica apenas no estado em memória.

## Organização

| Arquivo | Responsabilidade |
| --- | --- |
| `MainActivity.kt` | Iniciar a tela |
| `TelaLeitor.kt` | Exibir leituras e abrir conteúdo |
| `CameraQr.kt` | Câmera e análise dos quadros |
| `LeituraQr.kt` | Modelo e interpretação do ML Kit |
| `LeitorViewModel.kt` | Histórico e estado |
| `permissoes/ExigePermissao.kt` | Solicitar e acompanhar a permissão |
| `permissoes/PermissaoUtils.kt` | Consultar permissão e abrir configurações |

Os componentes de permissão foram implementados neste projeto, pois os arquivos do projeto PermissoesCamera não acompanharam o roteiro.

## Conferência em sala

| Código | Resultado esperado |
| --- | --- |
| 1 | Link com botão Abrir |
| 2 | Texto sem botão Abrir |
| 3 | Rede Wi-Fi: `Rede: Laboratorio-ADS`, sem senha nem botão Abrir |
| 4 | Telefone com abertura do discador |
| 5 | E-mail com abertura do aplicativo de e-mail |
| 6 | Localização com abertura de mapas |
| 7 | Texto registrado uma vez; releitura após outro código sobe ao topo |
| 8 | EAN-13 ignorado |

Confira também: negar a permissão duas vezes, voltar das configurações,
minimizar e retornar, girar a tela e limpar o histórico.

## Respostas diretas

1. **Permissão:** CAMERA é uma permissão perigosa. O manifesto declara o uso; o usuário autoriza em tempo de execução.
2. **Ciclo de vida:** ao minimizar, a câmera para. `LifecycleCameraController`, vinculado ao `LifecycleOwner`, acompanha o ciclo de vida e retoma a câmera.
3. **Filtro e thread:** o scanner aceita apenas `FORMAT_QR_CODE`. O callback usa o executor da thread principal.
4. **Interpretação:** o ML Kit identifica o tipo em `valueType`. O conversor monta a descrição e a URI. A senha não aparece para evitar exposição.
5. **Estado:** o ViewModel sobrevive à recriação da Activity na rotação. Uma nova lista mantém o estado imutável e permite ao StateFlow emitir a mudança.
6. **Discador:** `ACTION_VIEW` com `tel:` abre o discador para o usuário confirmar. `ACTION_CALL` faz a ligação diretamente e exige `CALL_PHONE`.

## Testes

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest lintDebug
.\gradlew.bat connectedDebugAndroidTest
```

Os testes locais verificam deduplicação, ordenação e limpeza.
O teste Android lê imagens dos oito códigos da folha com o ML Kit real,
verifica os tipos e as ações, o nome da rede e a rejeição do EAN-13.
As imagens ficam em `app/src/androidTest/assets` e não entram no APK principal.

Validação em 01/10/2026: APK compilado, três testes locais e um teste Android
aprovados no emulador Android 15 (API 35). Android Lint sem erros; há avisos
de versões e recomendações de configuração/estilo. A câmera ao vivo, os fluxos
de permissão, a rotação e a abertura dos aplicativos precisam da conferência em sala.

## Entrega

O ZIP de entrega deve conter o projeto sem `.gradle`, `build`, `.idea` e `local.properties`.
Renomeie para `LeitorQRCode-<nome1>-<nome2>.zip`, conforme o roteiro.

## Referências

- Roteiro e folha de códigos fornecidos pelo professor.
- [CameraX e MlKitAnalyzer](https://developer.android.com/reference/androidx/camera/mlkit/vision/MlKitAnalyzer).
- [Leitura de códigos com ML Kit](https://developers.google.com/ml-kit/vision/barcode-scanning/android).

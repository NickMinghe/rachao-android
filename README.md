# Rachão 🏀

App Android para organizar a pelada de basquete da rua: o jogador monta o perfil, escolhe uma quadra, entra no racha, sorteia os times e marca o placar ao vivo até 21 pontos.

Feito em **Kotlin** com **Views/XML**, navegação por **Intent explícita**, **ViewBinding** e **Fragment**. Os dados das quadras são simulados (`data/RachaoMock.kt`), sem backend.

## As 4 telas

| # | Tela | Classe | O que faz |
|---|------|--------|-----------|
| 1 | Perfil | `ui/perfil/PerfilActivity` (launcher) | Nome (obrigatório, com validação), apelido opcional, posição (Armador/Ala/Pivô) e nível de 1 a 5 com descrição que muda na hora. Botão "Bora pra quadra". |
| 2 | Quadras | `ui/quadras/QuadrasActivity` + `QuadrasFragment` | Saudação com o apelido (ou nome), lista de quadras em RecyclerView, filtro por chips (Todas / Cobertas / Ao ar livre) e contador de rachas rolando agora. |
| 3 | Detalhe da quadra | `ui/detalhe/QuadraDetalheActivity` | Informações da quadra (iluminação, regras da casa quando existirem), lista de confirmados, barra de ocupação, botão entrar/sair do racha e "Sortear times" a partir de 6 confirmados. |
| 4 | Placar | `ui/placar/PlacarActivity` | Placar Laranja × Azul até 21, botões +1/+2/+3 e desfazer por time, histórico das últimas cestas, banner de vencedor e "Nova partida". |

## Onde cada requisito foi atendido

- **Views/XML** — todas as telas são layouts XML em `app/src/main/res/layout/` (`activity_perfil.xml`, `fragment_quadras.xml`, `activity_quadra_detalhe.xml`, `activity_placar.xml`, itens de lista e o componente `view_botoes_pontos.xml`).
- **Intent explícita + putExtra**
  - Tela 1 → 2: `PerfilActivity.irPraQuadra()` envia o `Jogador` (Parcelable via `@Parcelize`).
  - Tela 2 → 3: `QuadrasFragment.abrirDetalhe()` envia o id da quadra e o jogador.
  - Tela 3 → 4: `QuadraDetalheActivity.sortearTimes()` envia os dois `Time` e o nome da quadra.
  - As chaves ficam centralizadas em `util/Extras.kt`; as telas 2, 3 e 4 têm `exported="false"` no `AndroidManifest.xml`.
- **ViewBinding** — habilitado em `app/build.gradle.kts` (`buildFeatures { viewBinding = true }`) e usado em todas as Activities, no Fragment e nos adapters. Não há `findViewById` no projeto.
- **Fragment** — `QuadrasFragment` contém a tela de quadras, recebe o jogador por `arguments` (`novaInstancia`) e usa o ciclo de vida `onCreateView` / `onViewCreated` / `onDestroyView`, liberando o binding no fim para evitar vazamento de memória. A `QuadrasActivity` só hospeda o fragment.
- **Interações que atualizam a interface** — descrição do nível (tela 1), filtro por chips (tela 2), entrar/sair e contador de vagas (tela 3), placar, desfazer e histórico (tela 4).
- **Opcionais / nulos** — apelido em branco vira `null` e a saudação usa `apelido ?: nome`; iluminação é `Boolean?` (Sim / Não / Não informado); "Regras da casa" só aparece quando existem.
- **Componente reutilizável** — `view_botoes_pontos.xml` é inflado uma vez para cada time no placar.
- **Estado em rotação** — detalhe e placar salvam o estado em `onSaveInstanceState`.

## Estrutura

```
app/src/main/java/com/nick/rachao/
├── adapter/   JogadorAdapter, QuadraAdapter
├── data/      RachaoMock (dados simulados)
├── model/     Jogador, Quadra, Time, Cesta, Posicao, TipoQuadra
├── ui/        perfil, quadras, detalhe, placar
└── util/      Extras, Sorteio, Extensoes
```

## Como rodar no Android Studio

1. Clone o repositório:
   ```bash
   git clone https://github.com/NickMinghe/rachao-android.git
   ```
2. No Android Studio, use **File › Open** e selecione a pasta `rachao-android`.
3. Aguarde o **Gradle Sync** terminar (o arquivo `local.properties` com o caminho do SDK é gerado automaticamente).
4. Crie ou selecione um emulador em **Device Manager** (API 24 ou superior), ou conecte um celular com depuração USB ativada.
5. Clique em **Run ▶** (`Shift + F10`) com a configuração `app`.

Também é possível compilar pela linha de comando:

```bash
./gradlew assembleDebug
```

O APK fica em `app/build/outputs/apk/debug/`.

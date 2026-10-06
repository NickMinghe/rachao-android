package com.nick.rachao.data

import com.nick.rachao.R
import com.nick.rachao.model.Jogador
import com.nick.rachao.model.Posicao
import com.nick.rachao.model.Quadra
import com.nick.rachao.model.TipoQuadra

/**
 * REQUISITO: dados simulados (mock) em um object — sem API e sem banco de dados.
 * Tudo aqui é imutável (List + data class com val): as telas criam cópias com copy().
 */
object RachaoMock {

    // Elenco fictício (alguns sem apelido = null, para exercitar os opcionais)
    val jogadores: List<Jogador> = listOf(
        Jogador(1, "Carlos Henrique", "Carlão", Posicao.PIVO, 4),
        Jogador(2, "Rafael Souza", "Rafa Três", Posicao.ALA, 5),
        Jogador(3, "Juliana Martins", null, Posicao.ARMADOR, 4),
        Jogador(4, "Diego Ferreira", "Mão de Alface", Posicao.ALA, 2),
        Jogador(5, "Thiago Lima", "Tchê", Posicao.ARMADOR, 3),
        Jogador(6, "Bruno Alves", "Paredão", Posicao.PIVO, 5),
        Jogador(7, "Larissa Costa", "Lari Splash", Posicao.ALA, 4),
        Jogador(8, "Pedro Rocha", null, Posicao.PIVO, 3),
        Jogador(9, "Marcos Vinícius", "Marquinho Crossover", Posicao.ARMADOR, 5),
        Jogador(10, "Ana Beatriz", "Bia", Posicao.ALA, 3),
        Jogador(11, "Gustavo Pereira", "Guga Garrafão", Posicao.PIVO, 2),
        Jogador(12, "Felipe Nunes", null, Posicao.ARMADOR, 1),
        Jogador(13, "Camila Ribeiro", "Cami", Posicao.ARMADOR, 4),
        Jogador(14, "Lucas Oliveira", "Luquinha Tabela", Posicao.ALA, 2),
        Jogador(15, "Vitor Hugo", "Vitão", Posicao.PIVO, 3)
    )

    private fun elenco(vararg ids: Int): List<Jogador> =
        ids.map { id -> jogadores.first { it.id == id } }

    val quadras: List<Quadra> = listOf(
        Quadra(
            id = 1,
            nome = "Quadra do Viaduto",
            bairro = "Centro",
            tipo = TipoQuadra.AO_AR_LIVRE,
            horario = "Seg a Sex · 19h às 22h",
            temIluminacao = true,
            regrasDaCasa = "Quem perde sai. Time que ganha fica até 3 vitórias seguidas.",
            rolandoAgora = true,
            vagas = 10,
            confirmados = elenco(1, 2, 4, 5, 7, 9, 14),
            fotoRes = R.drawable.ilustra_quadra_1
        ),
        Quadra(
            id = 2,
            nome = "Garrafão da Praça Central",
            bairro = "Vila Nova",
            tipo = TipoQuadra.AO_AR_LIVRE,
            horario = "Sáb · 8h às 11h",
            temIluminacao = null,
            regrasDaCasa = null,
            rolandoAgora = true,
            vagas = 10,
            confirmados = elenco(3, 6, 10, 11, 12),
            fotoRes = R.drawable.ilustra_quadra_2
        ),
        Quadra(
            id = 3,
            nome = "Ginásio do Bairro Alto",
            bairro = "Bairro Alto",
            tipo = TipoQuadra.COBERTA,
            horario = "Ter e Qui · 20h às 23h",
            temIluminacao = true,
            regrasDaCasa = "Proibido tênis de sola preta. Cesta de fora do garrafão vale 3.",
            rolandoAgora = false,
            vagas = 12,
            confirmados = elenco(1, 2, 3, 6, 8, 9, 13, 15, 7),
            fotoRes = R.drawable.ilustra_quadra_3
        ),
        Quadra(
            id = 4,
            nome = "Quadra da Laje",
            bairro = "Morro do Sol",
            tipo = TipoQuadra.AO_AR_LIVRE,
            horario = "Dom · 16h às 19h",
            temIluminacao = false,
            regrasDaCasa = "Chamou falta, a bola volta. Pendurar no aro é proibido!",
            rolandoAgora = false,
            vagas = 8,
            confirmados = elenco(4, 11, 14, 12),
            fotoRes = R.drawable.ilustra_quadra_4
        ),
        Quadra(
            id = 5,
            nome = "Arena Coberta Vila Esperança",
            bairro = "Vila Esperança",
            tipo = TipoQuadra.COBERTA,
            horario = "Qua · 18h30 às 21h30",
            temIluminacao = true,
            regrasDaCasa = null,
            rolandoAgora = true,
            vagas = 10,
            confirmados = elenco(5, 7, 8, 10, 13, 15),
            fotoRes = R.drawable.ilustra_quadra_5
        ),
        Quadra(
            id = 6,
            nome = "Cesta do Beco",
            bairro = "Jardim das Flores",
            tipo = TipoQuadra.AO_AR_LIVRE,
            horario = "Sex · 21h às 0h",
            temIluminacao = null,
            regrasDaCasa = "Cesta sem rede: na dúvida, quem arremessou decide.",
            rolandoAgora = false,
            vagas = 8,
            confirmados = elenco(2, 6, 9, 11, 14),
            fotoRes = R.drawable.ilustra_quadra_6
        )
    )

    /** Busca uma quadra pelo id recebido via Intent (pode não existir -> null). */
    fun buscarQuadra(id: Int): Quadra? = quadras.firstOrNull { it.id == id }
}

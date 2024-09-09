package br.com.alura.TabelaFipe.principal;

import br.com.alura.TabelaFipe.model.Dados;
import br.com.alura.TabelaFipe.model.Modelos;
import br.com.alura.TabelaFipe.model.Veiculo;
import br.com.alura.TabelaFipe.service.ConsumoApi;
import br.com.alura.TabelaFipe.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

// Classe principal que concentra todos os dados que serao chamados
public class Principal {
    private  Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private  final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu(){
        var menu = """
                *** OPÇÕES ****
                Carro
                Moto
                Caminhão
                
                Digite uma das opções para consultar:
                
                """;


        // Opção para o cliente escolher qual tipo ele quer escolher
        System.out.println(menu);
        var opcao = leitura.nextLine();
        String endereco;

        if(opcao.toLowerCase().contains("carr")){
            endereco =  URL_BASE + "carros/marcas";
        }
        else if(opcao.toLowerCase().contains("mot")){
            endereco =  URL_BASE + "motos/marcas";
        }
        else {
            endereco =  URL_BASE + "caminhoes/marcas";
        }

        var json = consumo.obterDados(endereco);
        System.out.println(json); // vai imprimir todas as informações em uma linha


        // Vai imprimir todos os dados em formato de uma lista para o cliente
        var marcas =  conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);


        // O usuario informa qual codigo para consulta e ele retorna os nome dos dados relacionados a esse codigo
        System.out.println("\nInforme o codigo da marca para a consulta: ");
        var condigoMarca = leitura.nextLine();

        endereco = endereco + "/" + condigoMarca + "/modelos";
        json = consumo.obterDados(endereco);
        var modeloLista =  conversor.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa marca: ");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);


        // Para o usuario digitar o trecho do nome do veiculo, e por meio dele ter todos os modelos daquele veiculo
        System.out.println("\nDigite um trecho do nome do carro a ser buscado: ");
        var nomeVeiculo = leitura.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m-> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos Filtrados: ");
        modelosFiltrados.forEach(System.out::println);


        // O usuario digita o codigo de um dos modelos de carro, e ira aparecer quais os anos daquele carro
        System.out.println("\nDigite o código do modelo para buscar os valores de avaliação: ");
        var codigoModelo = leitura.nextLine();

        endereco =  endereco + "/" + codigoModelo + "/anos";
        json = consumo.obterDados(endereco);
        List<Dados> anos = conversor.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for(int i = 0; i < anos.size(); i++){
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);

        }
        System.out.println("\nTodos os veículos filtrados com avaliações por ano: ");
        veiculos.forEach(System.out::println);
    }


}

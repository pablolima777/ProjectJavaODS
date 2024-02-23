package Teste0112;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main implements MensagemBoasVindas {
    public static void main(String[] args) {
        //objeto 1 da propria classe main
        Main main = new Main();
        main.exibirMensagemBoasVindas();
        Scanner scanner = new Scanner(System.in);


        try (Connection connection = Conexao.conectar()) {
            int opcao;

            do {
                exibirMenu();
                opcao = scanner.nextInt();
                scanner.nextLine(); // Limpar o buffer de entrada

                switch (opcao) {
                    case 1:
                        cadastrarDoacao(connection, scanner);
                        break;
                    case 2:
                        listarDoacoesDisponiveis(connection);
                        break;
                    case 3:
                        retirarDoacao(connection, scanner);
                        break;
                    case 4:
                        atualizarDoacao(connection, scanner);
                        break;
                    case 0:
                        System.out.println("Saindo do programa. Obrigado!");
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            } while (opcao != 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void exibirMenu() {
        System.out.println("========== Menu ==========");
        System.out.println("1. Cadastrar Doação");
        System.out.println("2. Doações Disponíveis");
        System.out.println("3. Retirar Doação");
        System.out.println("4. Atualizar Doação");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void cadastrarDoacao(Connection connection, Scanner scanner) {
        try {
            System.out.print("Digite o nome do doador: ");
            String nomeDoador = scanner.nextLine();

            System.out.print("Digite o endereço do doador: ");
            String enderecoDoador = scanner.nextLine();

            Pessoa pessoa = new Pessoa(nomeDoador, enderecoDoador);

            System.out.print("Digite o nome do alimento: ");
            String nomeAlimento = scanner.nextLine();

            System.out.print("Digite a quantidade de unidades disponíveis: ");
            int unidadesDisponiveis = scanner.nextInt();

            //Objeto 2
            ItemDoacao itemDoacao = new ItemDoacao(nomeAlimento, unidadesDisponiveis);

            //Cria uma instância de Doacao e chama o método cadastrarDoacao
            Doacao doacao = new Doacao(0, pessoa, itemDoacao);
            doacao.cadastrarDoacao(connection);

            System.out.println("Doação cadastrada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar doação: " + e.getMessage());
        }
    }

    private static void listarDoacoesDisponiveis(Connection connection) {
        try {
            List<Doacao> doacoes = Doacao.listarDoacoesDisponiveis(connection);

            if (doacoes.isEmpty()) {
                System.out.println("Não há doações disponíveis no momento.");
            } else {
                System.out.println("========== Doações Disponíveis ==========");
                for (Doacao doacao : doacoes) {
                    System.out.println("ID: " + doacao.getId());
                    System.out.println("Doador: " + doacao.getPessoa().getNome());
                    System.out.println("Endereço do Doador: " + doacao.getPessoa().getEndereco());
                    System.out.println("Item da Doação: " + doacao.getItemDoacao().getNomeAlimento());
                    System.out.println("Unidades Disponíveis: " + doacao.getItemDoacao().getUnidadesDisponiveis());
                    System.out.println("===============================");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar doações: " + e.getMessage());
        }
    }

    private static void retirarDoacao(Connection connection, Scanner scanner) {
        try {
            System.out.print("Digite o ID da doação que deseja retirar: ");
            int idDoacao = scanner.nextInt();
            Doacao retirarDoacao = Doacao.obterDoacaoPorId(connection, idDoacao);

            if (retirarDoacao != null) {
                Doacao.retirarDoacao(connection, idDoacao);
                System.out.println("Doação retirada com sucesso!");
            } else {
                System.out.println("Doação não encontrada.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao retirar doação: " + e.getMessage());
        }
    }
    private static void atualizarDoacao(Connection connection, Scanner scanner) {
        try {
            System.out.print("Digite o ID da doação que deseja atualizar: ");
            int idDoacao = scanner.nextInt();
            Doacao doacao = Doacao.obterDoacaoPorId(connection, idDoacao);

            if (doacao != null) {
                //Atualiza informações da doação
                atualizarInformacoesDoacao(scanner, doacao);

                //depois de  atualizar as informações, você pode atualizar a Doacao
                doacao.atualizarDoacao(connection);
                System.out.println("Doação atualizada com sucesso!");
            } else {
                System.out.println("Doação não encontrada.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar doação: " + e.getMessage());
        }
    }
    private static void atualizarInformacoesDoacao(Scanner scanner, Doacao doacao) {
        System.out.println("Digite as novas informações da doação:");

        System.out.print("Digite o novo nome do doador: ");
        String novoNomeDoador = scanner.nextLine();
        doacao.getPessoa().setNome(novoNomeDoador);

        System.out.print("Digite o novo endereço do doador: ");
        String novoEnderecoDoador = scanner.nextLine();
        doacao.getPessoa().setEndereco(novoEnderecoDoador);

        System.out.print("Digite o novo nome do alimento: ");
        String novoNomeAlimento = scanner.nextLine();
        doacao.getItemDoacao().setNomeAlimento(novoNomeAlimento);

        System.out.print("Digite a nova quantidade de unidades disponíveis: ");
        int novaQuantidade = scanner.nextInt();
        doacao.getItemDoacao().setUnidadesDisponiveis(novaQuantidade);
    }

    //Método sobrescrito
    @Override
    public void exibirMensagemBoasVindas() {
        System.out.println("Bem-vindo ao Sistema de Doações!");
    }
}

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Scanner;

public class App {

    static String nomeArquivoDados = "produtos.txt";
    static Scanner teclado = new Scanner(System.in, StandardCharsets.UTF_8);
    static Produto[] produtosCadastrados;
    static int quantosProdutos;
    static Pilha<Pedido> pilhaPedidos = new Pilha<>();

    static void cabecalho() {
        System.out.println("\nAEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }

    static void pausa() {
        System.out.println("\nDigite enter para continuar...");
        teclado.nextLine();
    }

    static int lerInteiro(String mensagem) {
        while (true) {
            System.out.println(mensagem);

            try {
                return Integer.parseInt(teclado.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar por um produto, por código");
        System.out.println("3 - Procurar por um produto, por nome");
        System.out.println("4 - Iniciar novo pedido");
        System.out.println("5 - Fechar pedido");
        System.out.println("6 - Listar produtos dos pedidos mais recentes");
        System.out.println("0 - Sair");
        return lerInteiro("Digite sua opção:");
    }

    static Produto[] lerProdutos(String nomeArquivoDados) {
        quantosProdutos = 0;

        try (Scanner arquivo = new Scanner(
                new File(nomeArquivoDados), StandardCharsets.UTF_8)) {

            int quantidade = Integer.parseInt(arquivo.nextLine().trim());

            if (quantidade < 0) {
                throw new IllegalArgumentException("Quantidade inválida.");
            }

            Produto[] produtos = new Produto[quantidade];

            for (int i = 0; i < quantidade; i++) {
                produtos[i] = Produto.criarDoTexto(arquivo.nextLine());
            }

            quantosProdutos = quantidade;
            return produtos;
        } catch (IOException | RuntimeException e) {
            System.out.println("Erro ao carregar produtos: " + e.getMessage());
            return new Produto[0];
        }
    }

    static Produto localizarProduto() {
        int codigo = lerInteiro("Digite o código do produto:");

        for (int i = 0; i < quantosProdutos; i++) {
            if (produtosCadastrados[i].hashCode() == codigo) {
                return produtosCadastrados[i];
            }
        }

        return null;
    }

    static Produto localizarProdutoDescricao() {
        System.out.println("Digite o nome ou a descrição do produto:");
        String descricao = teclado.nextLine().trim();

        for (int i = 0; i < quantosProdutos; i++) {
            if (produtosCadastrados[i].descricao.equalsIgnoreCase(descricao)) {
                return produtosCadastrados[i];
            }
        }

        return null;
    }

    static void mostrarProduto(Produto produto) {
        if (produto == null) {
            System.out.println("Produto não encontrado.");
        } else {
            System.out.println(produto);
        }
    }

    static void listarTodosOsProdutos() {
        if (quantosProdutos == 0) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }

        for (int i = 0; i < quantosProdutos; i++) {
            System.out.printf("%02d - %s%n", i + 1, produtosCadastrados[i]);
        }
    }

    public static Pedido iniciarPedido() {
        if (quantosProdutos == 0) {
            System.out.println("Não há produtos disponíveis.");
            return null;
        }

        int pagamento;

        do {
            pagamento = lerInteiro(
                "Forma de pagamento: 1 - À vista; 2 - A prazo"
            );
        } while (pagamento != 1 && pagamento != 2);

        Pedido pedido = new Pedido(LocalDate.now(), pagamento);
        int quantidadeProdutos;

        do {
            quantidadeProdutos = lerInteiro(
                "Quantos produtos serão incluídos? De 1 a 10."
            );
        } while (quantidadeProdutos < 1 || quantidadeProdutos > 10);

        for (int i = 0; i < quantidadeProdutos; i++) {
            Produto produto = localizarProdutoDescricao();

            if (produto == null) {
                System.out.println("Produto não encontrado.");
                i--;
                continue;
            }

            int quantidade;

            do {
                quantidade = lerInteiro("Quantas unidades desse produto?");
            } while (quantidade <= 0);

            if (pedido.incluirProduto(produto, quantidade)) {
                System.out.println("Produto incluído.");
            } else {
                System.out.println("Não foi possível incluir o produto.");
                i--;
            }
        }

        return pedido;
    }

    public static void finalizarPedido(Pedido pedido) {
        if (pedido == null) {
            System.out.println("Nenhum pedido em aberto.");
            return;
        }

        pilhaPedidos.empilhar(pedido);
        System.out.println("Pedido finalizado com sucesso.");
        System.out.println(pedido);
    }

    public static void listarProdutosPedidosRecentes() {
        if (pilhaPedidos.vazia()) {
            System.out.println("Nenhum pedido finalizado.");
            return;
        }

        int quantidade = lerInteiro(
            "Quantos pedidos recentes deseja consultar?"
        );

        if (quantidade <= 0) {
            System.out.println("Informe uma quantidade positiva.");
            return;
        }

        try {
            Pilha<Pedido> recentes = pilhaPedidos.subPilha(quantidade);

            while (!recentes.vazia()) {
                Pedido pedido = recentes.desempilhar();
                System.out.println("\nPedido: " + pedido.hashCode());

                for (ItemDePedido item : pedido.getItensDoPedido()) {
                    if (item != null) {
                        System.out.println(item.getProduto());
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        produtosCadastrados = lerProdutos(nomeArquivoDados);
        Pedido pedido = null;
        int opcao;

        do {
            opcao = menu();

            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> mostrarProduto(localizarProduto());
                case 3 -> mostrarProduto(localizarProdutoDescricao());
                case 4 -> {
                    if (pedido == null) {
                        pedido = iniciarPedido();
                    } else {
                        System.out.println("Finalize o pedido em aberto primeiro.");
                    }
                }
                case 5 -> {
                    finalizarPedido(pedido);
                    pedido = null;
                }
                case 6 -> listarProdutosPedidosRecentes();
                case 0 -> System.out.println("Programa encerrado.");
                default -> System.out.println("Opção inválida.");
            }

            if (opcao != 0) {
                pausa();
            }
        } while (opcao != 0);

        teclado.close();
    }
}
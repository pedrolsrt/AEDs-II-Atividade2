import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public class Fila<E> {

    private final Celula<E> frente;
    private Celula<E> tras;
    private int tamanho;

    public Fila() {
        frente = new Celula<>();
        tras = frente;
    }

    public boolean vazia() {
        return tamanho == 0;
    }

    public void enfileirar(E item) {
        Celula<E> nova = new Celula<>(item);
        tras.setProximo(nova);
        tras = nova;
        tamanho++;
    }

    public E consultarPrimeiro() {
        if (vazia()) {
            throw new NoSuchElementException("A fila está vazia.");
        }

        return frente.getProximo().getItem();
    }

    public E desenfileirar() {
        E item = consultarPrimeiro();
        frente.setProximo(frente.getProximo().getProximo());
        tamanho--;

        if (vazia()) {
            tras = frente;
        }

        return item;
    }

    private void validarQuantidade(int quantidade) {
        if (quantidade < 0 || quantidade > tamanho) {
            throw new IllegalArgumentException(
                "A quantidade deve estar entre 0 e " + tamanho + "."
            );
        }
    }

    public double calcularValorMedio(
            Function<E, Double> extrator, int quantidade) {

        validarQuantidade(quantidade);

        if (quantidade == 0) {
            throw new IllegalArgumentException(
                "Informe pelo menos um elemento para calcular a média."
            );
        }

        double soma = 0;
        Celula<E> atual = frente.getProximo();

        for (int i = 0; i < quantidade; i++) {
            soma += extrator.apply(atual.getItem());
            atual = atual.getProximo();
        }

        return soma / quantidade;
    }

    public Fila<E> filtrar(Predicate<E> condicional, int quantidade) {
        validarQuantidade(quantidade);

        Fila<E> resultado = new Fila<>();
        Celula<E> atual = frente.getProximo();

        for (int i = 0; i < quantidade; i++) {
            if (condicional.test(atual.getItem())) {
                resultado.enfileirar(atual.getItem());
            }

            atual = atual.getProximo();
        }

        return resultado;
    }
}
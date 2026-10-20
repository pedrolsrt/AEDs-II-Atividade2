import java.util.NoSuchElementException;

public class Pilha<E> {

    private Celula<E> topo;
    private final Celula<E> fundo;

    public Pilha() {
        fundo = new Celula<>();
        topo = fundo;
    }

    public boolean vazia() {
        return topo == fundo;
    }

    public void empilhar(E item) {
        topo = new Celula<>(item, topo);
    }

    public E desempilhar() {
        E item = consultarTopo();
        topo = topo.getProximo();
        return item;
    }

    public E consultarTopo() {
        if (vazia()) {
            throw new NoSuchElementException("Não há nenhum item na pilha.");
        }

        return topo.getItem();
    }

    public Pilha<E> subPilha(int numItens) {
        if (numItens < 0) {
            throw new IllegalArgumentException("Quantidade inválida.");
        }

        Pilha<E> auxiliar = new Pilha<>();
        Pilha<E> resultado = new Pilha<>();
        Celula<E> atual = topo;

        for (int i = 0; i < numItens; i++) {
            if (atual == fundo) {
                throw new IllegalArgumentException(
                    "A pilha não contém essa quantidade de elementos."
                );
            }

            auxiliar.empilhar(atual.getItem());
            atual = atual.getProximo();
        }

        while (!auxiliar.vazia()) {
            resultado.empilhar(auxiliar.desempilhar());
        }

        return resultado;
    }
}
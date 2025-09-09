package ahodanenok.el.token;

import java.util.LinkedList;

public class LookaheadTokenizer {

    private final Tokenizer tokenizer;
    private final LinkedList<Token> tokensPending;
    private final int lookaheadLimit;

    public LookaheadTokenizer(Tokenizer tokenizer, int lookaheadLimit) {
        this.tokenizer = tokenizer;
        this.tokensPending = new LinkedList<>();
        this.lookaheadLimit = lookaheadLimit;
    }

    public boolean hasNext() {
        return !tokensPending.isEmpty() || tokenizer.hasNext();
    }

    public Token next() {
        if (!tokensPending.isEmpty()) {
            return tokensPending.poll();
        } else {
            return tokenizer.next();
        }
    }

    public Token peek(int lookahead) {
        if (lookahead > lookaheadLimit) {
            throw new IllegalStateException(
                "Lookahead count of %s is greater than the limit of %s"
                    .formatted(lookahead, lookaheadLimit));
        }

        while (tokensPending.size() < lookahead && tokenizer.hasNext()) {
            tokensPending.offer(tokenizer.next());
        }

        if (tokensPending.size() < lookahead) {
            return null;
        }

        return tokensPending.get(lookahead - 1);
    }
}

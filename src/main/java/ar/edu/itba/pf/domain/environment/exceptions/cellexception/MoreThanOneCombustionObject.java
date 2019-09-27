package ar.edu.itba.pf.domain.environment.exceptions.cellexception;

import ar.edu.itba.pf.domain.environment.impl.Cell;

public class MoreThanOneCombustionObject extends CellException {
    public MoreThanOneCombustionObject(Cell cell) {
        super(cell);
    }

    @Override
    public String getMessage() {
        return String.format("%s: Ya hay un objeto combustible en esta", super.getMessage());
    }
}

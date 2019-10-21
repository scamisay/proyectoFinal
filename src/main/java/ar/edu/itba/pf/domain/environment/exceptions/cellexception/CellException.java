package ar.edu.itba.pf.domain.environment.exceptions.cellexception;

import ar.edu.itba.pf.domain.environment.impl.Cell;

public class CellException extends RuntimeException{

    private Cell cell;

    public CellException(Cell cell) {
        this.cell = cell;
    }

    @Override
    public String getMessage() {
        return String.format("%s",cell.toString());
    }
}

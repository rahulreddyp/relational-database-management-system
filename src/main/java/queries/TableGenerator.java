package queries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableGenerator {

  private final int anInt = 2;

  private void createRow(StringBuilder sBuilder, int headersSize, Map<Integer, Integer> columnWidth) {
    int i = 0;
    while (i < headersSize) {
      String tableJoin = "+";
      if (i == 0) {
        sBuilder.append(tableJoin);
      }

      int j = 0;
      while (j < columnWidth.get(i) + anInt * 2) {
        String TABLE_H_SPLIT_SYMBOL = "-";
        sBuilder.append(TABLE_H_SPLIT_SYMBOL);
        j++;
      }
      sBuilder.append(tableJoin);
      i++;
    }
  }

  public String generateTable(List<String> headers, List<List<String>> rows, int... headerHeight) {
    int index = 0;
    int rowHeight = headerHeight.length > 0 ? headerHeight[0] : 1;
    StringBuilder sBuilder = new StringBuilder();
    Map<Integer, Integer> columnWidth = maxWidth(headers, rows);

    String n = "\n";
    sBuilder.append(n);
    sBuilder.append(n);
    createRow(sBuilder, headers.size(), columnWidth);
    sBuilder.append(n);

    while (index < headers.size()) {
      fillValuesToCell(sBuilder, headers.get(index), index, columnWidth);
      index++;
    }
    sBuilder.append(n);

    createRow(sBuilder, headers.size(), columnWidth);

    for (List<String> row : rows) {
      sBuilder.append(n.repeat(Math.max(0, rowHeight)));
      for (int cellIndex = 0; cellIndex < row.size(); cellIndex++) {
        fillValuesToCell(sBuilder, row.get(cellIndex), cellIndex, columnWidth);
      }
    }

    sBuilder.append(n);
    createRow(sBuilder, headers.size(), columnWidth);
    sBuilder.append(n);
    sBuilder.append(n);
    return sBuilder.toString();
  }

  private void addSpace(int length, StringBuilder sBuilder) {
    sBuilder.append(" ".repeat(Math.max(0, length)));
  }

  private Map<Integer, Integer> maxWidth(List<String> headers, List<List<String>> rows) {
    Map<Integer, Integer> columnWidth = new HashMap<>();

    for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
      columnWidth.put(columnIndex, 0);
    }

    for (int columnIndex = 0; columnIndex < headers.size(); columnIndex++) {
      if (headers.get(columnIndex).length() <= columnWidth.get(columnIndex)) {
        continue;
      }
      columnWidth.put(columnIndex, headers.get(columnIndex).length());
    }

    for (int i = 0, rowsSize = rows.size(); i < rowsSize; i++) {
      List<String> row = rows.get(i);
      for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
        if (row.get(columnIndex).length() > columnWidth.get(columnIndex)) {
          columnWidth.put(columnIndex, row.get(columnIndex).length());
        }
      }
    }

    int columnIndex = 0;
    while (columnIndex < headers.size()) {
      if (columnWidth.get(columnIndex) % 2 != 0) {
        columnWidth.put(columnIndex, columnWidth.get(columnIndex) + 1);
      }
      columnIndex++;
    }
    return columnWidth;
  }

  private void fillValuesToCell(StringBuilder sBuilder, String cell, int cellIndex, Map<Integer, Integer> columnWidth) {

    int cellPaddingSize = getCellPadding(cellIndex, cell.length(), columnWidth, anInt);

    String tableBorder = "|";
    if (cellIndex == 0) {
      sBuilder.append(tableBorder);
    }

    addSpace(cellPaddingSize, sBuilder);
    sBuilder.append(cell);
    if (cell.length() % 2 != 0) {
      sBuilder.append(" ");
    }

    addSpace(cellPaddingSize, sBuilder);
    sBuilder.append(tableBorder);
  }

  private int getCellPadding(int cellIndex, int dataLength, Map<Integer, Integer> columnWidth, int cellPaddingSize) {
    if (dataLength % 2 != 0) {
      dataLength++;
    }

    if (dataLength < columnWidth.get(cellIndex)) {
      cellPaddingSize = cellPaddingSize + (columnWidth.get(cellIndex) - dataLength) / 2;
    }
    return cellPaddingSize;
  }
}

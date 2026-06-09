package com.example.livestock.report;

import java.util.List;

public record ReportData(String title, List<String> headers, List<ReportRow> rows) {
}

package com.budgettracker.dto;

public record TransactionRequest(String amount, String description, String date, Long categoryId) {}
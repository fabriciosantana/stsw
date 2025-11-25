package com.example.demo;

import spark.ResponseTransformer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

class StaticPageRenderer implements ResponseTransformer {
  private final String resourceName;
  StaticPageRenderer(String resourceName) { this.resourceName = resourceName; }

  @Override public String render(Object o) {
    try (var is = getClass().getResourceAsStream("/public/" + resourceName)) {
      if (is == null) return "<h1>Arquivo não encontrado</h1>";
      var br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
      var sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) sb.append(line).append("\n");
      return sb.toString();
    } catch (Exception e) {
      return "<h1>Erro carregando página</h1>";
    }
  }
}

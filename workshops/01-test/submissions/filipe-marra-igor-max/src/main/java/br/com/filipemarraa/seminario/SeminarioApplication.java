package br.com.filipemarraa.seminario;

public final class SeminarioApplication {

    private SeminarioApplication() {
    }

    public static void main(String[] args) {
        SecurityScoreCalculator calculator = new SecurityScoreCalculator();
        int score = calculator.calculateScore(true, true, 1, 15);
        RiskLevel riskLevel = calculator.classifyRisk(score);

        System.out.println("Security score: " + score);
        System.out.println("Risk level: " + riskLevel);
    }
}


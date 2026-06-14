package br.com.filipemarraa.seminario;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SecurityScoreCalculatorTest {

    private final SecurityScoreCalculator calculator = new SecurityScoreCalculator();

    @Test
    void shouldReturnMaximumScoreWhenEverythingIsCompliant() {
        int score = calculator.calculateScore(true, true, 0, 0);
        assertEquals(100, score);
        assertEquals(RiskLevel.LOW, calculator.classifyRisk(score));
    }

    @Test
    void shouldApplyExpectedPenaltiesForCommonScenario() {
        int score = calculator.calculateScore(false, true, 2, 45);

        assertEquals(59, score);
        assertEquals(RiskLevel.HIGH, calculator.classifyRisk(score));
    }

    @Test
    void shouldCapVulnerabilityPenalty() {
        int score = calculator.calculateScore(true, true, 99, 0);
        assertEquals(60, score);
    }

    @Test
    void shouldCapPatchPenalty() {
        int score = calculator.calculateScore(true, true, 0, 365);
        assertEquals(75, score);
    }

    @Test
    void shouldNeverReturnNegativeScore() {
        int score = calculator.calculateScore(false, false, 99, 365);
        assertEquals(0, score);
        assertEquals(RiskLevel.CRITICAL, calculator.classifyRisk(score));
    }

    @Test
    void shouldClassifyBoundaryValues() {
        assertEquals(RiskLevel.LOW, calculator.classifyRisk(85));
        assertEquals(RiskLevel.MEDIUM, calculator.classifyRisk(84));
        assertEquals(RiskLevel.MEDIUM, calculator.classifyRisk(70));
        assertEquals(RiskLevel.HIGH, calculator.classifyRisk(69));
        assertEquals(RiskLevel.HIGH, calculator.classifyRisk(50));
        assertEquals(RiskLevel.CRITICAL, calculator.classifyRisk(49));
    }

    @Test
    void shouldRejectInvalidScoreForClassification() {
        assertThrows(IllegalArgumentException.class, () -> calculator.classifyRisk(-1));
        assertThrows(IllegalArgumentException.class, () -> calculator.classifyRisk(101));
    }

    @Test
    void shouldRejectNegativeInputsForCalculation() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateScore(true, true, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateScore(true, true, 0, -1));
    }
}

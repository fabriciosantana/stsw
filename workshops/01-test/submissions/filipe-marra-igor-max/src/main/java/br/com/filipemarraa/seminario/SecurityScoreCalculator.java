package br.com.filipemarraa.seminario;

public class SecurityScoreCalculator {

    public int calculateScore(
            boolean mfaEnabled,
            boolean strongPassword,
            int openVulnerabilities,
            int daysWithoutPatch
    ) {
        validateNonNegative(openVulnerabilities, "openVulnerabilities");
        validateNonNegative(daysWithoutPatch, "daysWithoutPatch");

        int score = 100;

        if (!mfaEnabled) {
            score -= 20;
        }
        if (!strongPassword) {
            score -= 15;
        }

        int vulnerabilityPenalty = Math.min(openVulnerabilities * 8, 40);
        score -= vulnerabilityPenalty;

        if (daysWithoutPatch > 30) {
            int overdueWindows = ((daysWithoutPatch - 31) / 15) + 1;
            int patchPenalty = Math.min(overdueWindows * 5, 25);
            score -= patchPenalty;
        }

        return Math.max(score, 0);
    }

    public RiskLevel classifyRisk(int score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("score must be between 0 and 100");
        }

        if (score >= 85) {
            return RiskLevel.LOW;
        }
        if (score >= 70) {
            return RiskLevel.MEDIUM;
        }
        if (score >= 50) {
            return RiskLevel.HIGH;
        }
        return RiskLevel.CRITICAL;
    }

    private void validateNonNegative(int value, String parameterName) {
        if (value < 0) {
            throw new IllegalArgumentException(parameterName + " must be greater than or equal to zero");
        }
    }
}


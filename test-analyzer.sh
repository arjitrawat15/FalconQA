#!/bin/bash

# FalconQA Test Execution Analyzer
# Runs all test suites and generates comprehensive analysis

echo "════════════════════════════════════════════════════════════════"
echo "   🚀 FalconQA Test Execution Analyzer"
echo "════════════════════════════════════════════════════════════════"
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Create results directory
RESULTS_DIR="test-execution-results"
mkdir -p $RESULTS_DIR

# Function to run tests and capture results
run_test_suite() {
    local suite_name=$1
    local suite_file=$2
    local output_file="$RESULTS_DIR/${suite_name}-results.txt"
    
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${YELLOW}📋 Running: $suite_name${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
    
    START_TIME=$(date +%s)
    
    if [ -z "$suite_file" ]; then
        # Run default suite
        mvn clean test > "$output_file" 2>&1
    else
        # Run specific suite
        mvn clean test -DsuiteXmlFile="$suite_file" > "$output_file" 2>&1
    fi
    
    TEST_RESULT=$?
    END_TIME=$(date +%s)
    DURATION=$((END_TIME - START_TIME))
    
    # Parse results from output
    TOTAL=$(grep -oP "Tests run: \K\d+" "$output_file" | head -1)
    FAILURES=$(grep -oP "Failures: \K\d+" "$output_file" | head -1)
    ERRORS=$(grep -oP "Errors: \K\d+" "$output_file" | head -1)
    SKIPPED=$(grep -oP "Skipped: \K\d+" "$output_file" | head -1)
    
    # Default to 0 if not found
    TOTAL=${TOTAL:-0}
    FAILURES=${FAILURES:-0}
    ERRORS=${ERRORS:-0}
    SKIPPED=${SKIPPED:-0}
    PASSED=$((TOTAL - FAILURES - ERRORS - SKIPPED))
    
    # Calculate percentage
    if [ $TOTAL -gt 0 ]; then
        PASS_RATE=$((PASSED * 100 / TOTAL))
    else
        PASS_RATE=0
    fi
    
    # Display results
    echo -e "${GREEN}✅ Passed:${NC}  $PASSED/$TOTAL ($PASS_RATE%)"
    echo -e "${RED}❌ Failed:${NC}  $FAILURES"
    echo -e "${RED}⚠️  Errors:${NC}  $ERRORS"
    echo -e "${YELLOW}⏭️  Skipped:${NC} $SKIPPED"
    echo -e "${BLUE}⏱️  Duration:${NC} ${DURATION}s"
    echo ""
    
    # Save summary
    echo "$suite_name|$TOTAL|$PASSED|$FAILURES|$ERRORS|$SKIPPED|$DURATION|$PASS_RATE" >> "$RESULTS_DIR/summary.csv"
    
    return $TEST_RESULT
}

# Initialize summary CSV
echo "Suite|Total|Passed|Failed|Errors|Skipped|Duration(s)|PassRate(%)" > "$RESULTS_DIR/summary.csv"

# Run all test suites
echo -e "${GREEN}Starting test execution...${NC}"
echo ""

# 1. Smoke Tests
run_test_suite "Smoke Tests" "src/test/resources/smoke-tests.xml"
SMOKE_RESULT=$?

# 2. Data-Driven Tests
run_test_suite "Data-Driven Tests" "src/test/resources/data-driven-tests.xml"
DD_RESULT=$?

# 3. Full Test Suite
run_test_suite "Full Test Suite" "src/test/resources/testng.xml"
FULL_RESULT=$?

# Generate final report
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}📊 EXECUTION SUMMARY${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# Read and display summary
column -t -s'|' "$RESULTS_DIR/summary.csv"

echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

# Calculate overall result
if [ $SMOKE_RESULT -eq 0 ] && [ $DD_RESULT -eq 0 ] && [ $FULL_RESULT -eq 0 ]; then
    echo -e "${GREEN}✅ All test suites PASSED!${NC}"
    exit 0
else
    echo -e "${RED}❌ Some test suites FAILED${NC}"
    echo -e "${YELLOW}Check detailed logs in: $RESULTS_DIR/${NC}"
    exit 1
fi

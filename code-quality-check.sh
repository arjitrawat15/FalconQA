#!/bin/bash

# FalconQA Code Quality Analyzer
# Analyzes code quality metrics

echo "════════════════════════════════════════════════════════════════"
echo "   🔍 FalconQA Code Quality Analyzer"
echo "════════════════════════════════════════════════════════════════"
echo ""

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Count files
echo -e "${BLUE}📁 Project Structure:${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

JAVA_FILES=$(find src -name "*.java" | wc -l)
TEST_FILES=$(find src/test -name "*Test*.java" | wc -l)
PAGE_FILES=$(find src/main/java -path "*/pages/*.java" | wc -l)
UTIL_FILES=$(find src/main/java -path "*/utils/*.java" | wc -l)
CORE_FILES=$(find src/main/java -path "*/core/*.java" | wc -l)

echo "   Total Java Files:        $JAVA_FILES"
echo "   Test Files:              $TEST_FILES"
echo "   Page Object Files:       $PAGE_FILES"
echo "   Utility Files:           $UTIL_FILES"
echo "   Core Framework Files:    $CORE_FILES"
echo ""

# Count lines of code
echo -e "${BLUE}📊 Lines of Code:${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

TOTAL_LINES=$(find src -name "*.java" -exec cat {} \; | wc -l)
TEST_LINES=$(find src/test -name "*.java" -exec cat {} \; | wc -l)
PROD_LINES=$((TOTAL_LINES - TEST_LINES))

echo "   Total Lines:             $TOTAL_LINES"
echo "   Production Code Lines:   $PROD_LINES"
echo "   Test Code Lines:         $TEST_LINES"
echo ""

# Check for TODO comments
echo -e "${BLUE}📝 TODO Comments:${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

TODO_COUNT=$(grep -r "TODO" src --include="*.java" | wc -l)
if [ $TODO_COUNT -gt 0 ]; then
    echo -e "${YELLOW}   Found $TODO_COUNT TODO comments:${NC}"
    grep -rn "TODO" src --include="*.java" | head -10
    if [ $TODO_COUNT -gt 10 ]; then
        echo "   ... and $((TODO_COUNT - 10)) more"
    fi
else
    echo -e "${GREEN}   ✅ No TODO comments found${NC}"
fi
echo ""

# Check for commented code
echo -e "${BLUE}💬 Commented Code Analysis:${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

COMMENTED_LINES=$(grep -r "//" src --include="*.java" | grep -v "///" | wc -l)
echo "   Single-line comments:    $COMMENTED_LINES"

MULTILINE_COMMENTS=$(grep -r "/\*" src --include="*.java" | wc -l)
echo "   Multi-line comment blocks: $MULTILINE_COMMENTS"
echo ""

# Check JavaDoc coverage
echo -e "${BLUE}📚 JavaDoc Coverage:${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

PUBLIC_METHODS=$(grep -r "public.*(" src/main/java --include="*.java" | grep -v "public class" | grep -v "public interface" | wc -l)
JAVADOC_COMMENTS=$(grep -r "/\*\*" src/main/java --include="*.java" | wc -l)

if [ $PUBLIC_METHODS -gt 0 ]; then
    JAVADOC_COVERAGE=$((JAVADOC_COMMENTS * 100 / PUBLIC_METHODS))
    echo "   Public Methods:          $PUBLIC_METHODS"
    echo "   JavaDoc Comments:        $JAVADOC_COMMENTS"
    echo "   Coverage:                $JAVADOC_COVERAGE%"
    
    if [ $JAVADOC_COVERAGE -gt 80 ]; then
        echo -e "   ${GREEN}✅ Excellent JavaDoc coverage!${NC}"
    elif [ $JAVADOC_COVERAGE -gt 50 ]; then
        echo -e "   ${YELLOW}⚠️  Good, but could be improved${NC}"
    else
        echo -e "   ${YELLOW}⚠️  JavaDoc coverage needs improvement${NC}"
    fi
else
    echo "   No public methods found"
fi
echo ""

# Check for code complexity (long methods)
echo -e "${BLUE}🔄 Code Complexity:${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

LONG_METHODS=$(find src -name "*.java" -exec awk '/\{/,/\}/ {lines++} /^[[:space:]]*\}[[:space:]]*$/ {if (lines > 50) print FILENAME":"NR" ("lines" lines)"; lines=0}' {} \; | wc -l)

if [ $LONG_METHODS -gt 0 ]; then
    echo -e "${YELLOW}   ⚠️  Found $LONG_METHODS methods with >50 lines${NC}"
    echo "   Consider refactoring for better maintainability"
else
    echo -e "${GREEN}   ✅ No overly long methods found${NC}"
fi
echo ""

# Check test coverage
echo -e "${BLUE}🧪 Test Coverage Analysis:${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

TEST_METHODS=$(grep -r "@Test" src/test --include="*.java" | wc -l)
PAGE_OBJECTS=$(find src/main/java/com/falconqa/pages -name "*.java" | wc -l)

echo "   Test Methods:            $TEST_METHODS"
echo "   Page Objects:            $PAGE_OBJECTS"

if [ $PAGE_OBJECTS -gt 0 ]; then
    TESTS_PER_PAGE=$((TEST_METHODS / PAGE_OBJECTS))
    echo "   Tests per Page Object:   ~$TESTS_PER_PAGE"
fi
echo ""

# Check for code smells
echo -e "${BLUE}👃 Code Smell Detection:${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# System.out.println (should use logger)
PRINTLN_COUNT=$(grep -r "System.out.println" src/main --include="*.java" | wc -l)
if [ $PRINTLN_COUNT -gt 0 ]; then
    echo -e "${YELLOW}   ⚠️  Found $PRINTLN_COUNT System.out.println (use logger instead)${NC}"
else
    echo -e "${GREEN}   ✅ No System.out.println found${NC}"
fi

# printStackTrace (should use logger)
PRINTSTACKTRACE=$(grep -r "printStackTrace" src --include="*.java" | wc -l)
if [ $PRINTSTACKTRACE -gt 0 ]; then
    echo -e "${YELLOW}   ⚠️  Found $PRINTSTACKTRACE printStackTrace calls (use logger)${NC}"
else
    echo -e "${GREEN}   ✅ No printStackTrace found${NC}"
fi

# Thread.sleep (should use explicit waits)
THREAD_SLEEP=$(grep -r "Thread.sleep" src/main --include="*.java" | grep -v "pause" | wc -l)
if [ $THREAD_SLEEP -gt 0 ]; then
    echo -e "${YELLOW}   ⚠️  Found $THREAD_SLEEP Thread.sleep calls (use explicit waits)${NC}"
else
    echo -e "${GREEN}   ✅ No Thread.sleep found (good!)${NC}"
fi

echo ""

# Overall quality score
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}📊 OVERALL CODE QUALITY SCORE${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

# Calculate score (simple heuristic)
SCORE=100

# Deduct for TODOs
SCORE=$((SCORE - TODO_COUNT))

# Deduct for code smells
SCORE=$((SCORE - PRINTLN_COUNT * 2))
SCORE=$((SCORE - PRINTSTACKTRACE * 2))
SCORE=$((SCORE - THREAD_SLEEP * 3))

# Bonus for JavaDoc
if [ $JAVADOC_COVERAGE -gt 80 ]; then
    SCORE=$((SCORE + 10))
fi

# Bonus for good test coverage
if [ $TEST_METHODS -gt 50 ]; then
    SCORE=$((SCORE + 10))
fi

# Cap at 100
if [ $SCORE -gt 100 ]; then
    SCORE=100
fi

# Cap at 0
if [ $SCORE -lt 0 ]; then
    SCORE=0
fi

echo ""
if [ $SCORE -gt 90 ]; then
    echo -e "${GREEN}   🏆 EXCELLENT: $SCORE/100${NC}"
elif [ $SCORE -gt 75 ]; then
    echo -e "${GREEN}   ✅ GOOD: $SCORE/100${NC}"
elif [ $SCORE -gt 60 ]; then
    echo -e "${YELLOW}   ⚠️  FAIR: $SCORE/100${NC}"
else
    echo -e "${YELLOW}   ⚠️  NEEDS IMPROVEMENT: $SCORE/100${NC}"
fi

echo ""
echo -e "${BLUE}════════════════════════════════════════════════════════════════${NC}"
echo ""

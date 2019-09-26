package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType.INT

/**
 * Nodes that previously corresponded to ASTAllocationExpression.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTArrayAllocationTest : ParserTestSpec({

    parserTest("Array creation") {

        "new int[2][]" should matchExpr<ASTArrayAllocation> {
            it::getTypeNode shouldBe arrayType({ primitiveType(INT) }) {
                dimExpr {
                    int(2)
                }
                arrayDim()
            }
        }

        "new @Foo int[3][2]" should matchExpr<ASTArrayAllocation> {

            it::getTypeNode shouldBe arrayType({
                primitiveType(INT) {
                    annotation("Foo")
                }
            }) {
                dimExpr {
                    int(3)
                }
                dimExpr {
                    int(2)
                }
            }
        }
        "new @Foo int @Bar [3][2]" should matchExpr<ASTArrayAllocation> {


            it::getTypeNode shouldBe arrayType({
                primitiveType(INT) {
                    annotation("Foo")
                }
            }) {
                dimExpr {
                    annotation("Bar")
                    int(3)
                }
                dimExpr {
                    int(2)
                }
            }
        }

        "(new int[3])[2]" should matchExpr<ASTArrayAccess> {
            parenthesized {

                child<ASTArrayAllocation> {
                    it::getTypeNode shouldBe arrayType({ primitiveType(INT) }) {
                        dimExpr {
                            int(3)
                        }
                    }
                }
            }

            it::getIndexExpression shouldBe int(2)
        }

        "new Foo[0]" should matchExpr<ASTArrayAllocation> {
            it::getTypeNode shouldBe arrayType({
                it::getArrayDepth shouldBe 1
                classType("Foo")
            }) {
                dimExpr {
                    int(0)
                }
            }
        }

    }

    parserTest("With array initializer") {

        "new Foo[] { f, g }" should matchExpr<ASTArrayAllocation> {

            it::getArrayDepth shouldBe 1

            it::getTypeNode shouldBe arrayType({
                it::getArrayDepth shouldBe 1
                classType("Foo")
            }) {
                arrayDim()
            }

            it::getArrayInitializer shouldBe child {
                variableAccess("f")
                variableAccess("g")
            }
        }

        "new int[][] { { 1 }, { 2 } }" should matchExpr<ASTArrayAllocation> {
            it::getArrayDepth shouldBe 2

            it::getTypeNode shouldBe arrayType({
                it::getArrayDepth shouldBe 2
                primitiveType(INT)
            }) {
                arrayDim()
                arrayDim()
            }

            it::getArrayInitializer shouldBe child {
                child<ASTArrayInitializer> {
                    int(1)
                }

                child<ASTArrayInitializer> {
                    int(2)
                }
            }
        }

        "new int[][] { { 1 , 2 }, null }" should matchExpr<ASTArrayAllocation> {
            it::getArrayDepth shouldBe 2

            it::getTypeNode shouldBe arrayType({
                it::getArrayDepth shouldBe 2
                primitiveType(INT)
            }) {
                arrayDim()
                arrayDim()
            }

            it::getArrayInitializer shouldBe child {
                child<ASTArrayInitializer> {
                    int(1)
                    int(2)
                }
                child<ASTNullLiteral> {}
            }
        }
    }
})

package com.selflearningcoursecreationapp.utils

import android.graphics.Path
import android.util.Log
import kotlin.math.*

class PathNodeArc {

    companion object {
        private const val LOGTAG = "PathDataNode"

        fun with(path: Path): Builder {
            return Builder(path)
        }
    }

    class Builder(private var path: Path) {
        private var x0: Float = 0f
        private var x1: Float = 0f
        private var y0: Float = 0f
        private var y1: Float = 0f
        private var a: Float = 0f
        private var b: Float = 0f
        private var theta: Float = 0f
        private var isMoreThanHalf: Boolean = false
        private var isPositiveArc: Boolean = false


        fun setCurrentX(x: Float): Builder {
            x0 = x
            return this
        }

        fun setCurrentY(y: Float): Builder {
            y0 = y
            return this
        }

        fun setX1(x: Float): Builder {
            x1 = x
            return this
        }

        fun setY1(y: Float): Builder {
            y1 = y
            return this
        }

        fun setA(a: Float): Builder {
            this.a = a
            return this
        }

        fun setB(b: Float): Builder {
            this.b = b
            return this
        }

        fun setTheta(value: Float): Builder {
            this.theta = value
            return this
        }

        fun setMoreThanHalf(b: Boolean): Builder {
            isMoreThanHalf = b
            return this
        }

        fun setPositiveArc(b: Boolean): Builder {
            isPositiveArc = b
            return this
        }

        fun drawArc() {
            /* Convert rotation angle from degrees to radians */
            val thetaD = Math.toRadians(theta.toDouble())
            /* Pre-compute rotation matrix entries */
            val cosTheta = cos(thetaD)
            val sinTheta = sin(thetaD)
            /* Transform (x0, y0) and (x1, y1) into unit space */
            /* using (inverse) rotation, followed by (inverse) scale */
            val x0p = (x0 * cosTheta + y0 * sinTheta) / a
            val y0p = (-x0 * sinTheta + y0 * cosTheta) / b
            val x1p = (x1 * cosTheta + y1 * sinTheta) / a
            val y1p = (-x1 * sinTheta + y1 * cosTheta) / b

            /* Compute differences and averages */
            val dx = x0p - x1p
            val dy = y0p - y1p
            val xm = (x0p + x1p) / 2
            val ym = (y0p + y1p) / 2
            /* Solve for intersecting unit circles */
            val dsq = dx * dx + dy * dy
            if (dsq == 0.0) {
                Log.w(LOGTAG, " Points are coincident")
                return  /* Points are coincident */
            }
            val disc = 1.0 / dsq - 1.0 / 4.0
            if (disc < 0.0) {
                Log.w(LOGTAG, "Points are too far apart $dsq")
                val adjust = (sqrt(dsq) / 1.99999).toFloat()
                drawArc(
//                  path, x0, y0, x1, y1, a * adjust,
//                  b * adjust, theta, isMoreThanHalf, isPositiveArc
                )
                return  /* Points are too far apart */
            }
            val s = sqrt(disc)
            val sdx = s * dx
            val sdy = s * dy
            var cx: Double
            var cy: Double
            if (isMoreThanHalf == isPositiveArc) {
                cx = xm - sdy
                cy = ym + sdx
            } else {
                cx = xm + sdy
                cy = ym - sdx
            }

            val eta0 = atan2(y0p - cy, x0p - cx)

            val eta1 = atan2(y1p - cy, x1p - cx)

            var sweep = eta1 - eta0
            if (isPositiveArc != sweep >= 0) {
                if (sweep > 0) {
                    sweep -= 2 * Math.PI
                } else {
                    sweep += 2 * Math.PI
                }
            }

            cx *= a
            cy *= b
            val tcx = cx
            cx = cx * cosTheta - cy * sinTheta
            cy = tcx * sinTheta + cy * cosTheta
            arcToBezier(
//                path,
                cx,
                cy,
//                a.toDouble(),
//                b.toDouble(),
                x0.toDouble(),
                y0.toDouble(),
                thetaD,
                eta0,
                sweep
            )
        }


        private fun arcToBezier(
//            p: Path,
            cx: Double,
            cy: Double,
//            a: Double,
//            b: Double,
            e1x: Double,
            e1y: Double,
            theta: Double,
            start: Double,
            sweep: Double
        ) {
            // Taken from equations at: http://spaceroots.org/documents/ellipse/node8.html
            // and http://www.spaceroots.org/documents/ellipse/node22.html

            // Maximum of 45 degrees per cubic Bezier segment
            val numSegments = ceil(abs(sweep * 4 / Math.PI)).toInt()

            var e1x = e1x
            var e1y = e1y
            var eta1 = start
            val cosTheta = cos(theta)
            val sinTheta = sin(theta)
            val cosEta1 = cos(eta1)
            val sinEta1 = sin(eta1)
            var ep1x = (-a * cosTheta * sinEta1) - (b * sinTheta * cosEta1)
            var ep1y = (-a * sinTheta * sinEta1) + (b * cosTheta * cosEta1)

            val anglePerSegment = sweep / numSegments
            for (i in 0 until numSegments) {
                val eta2 = eta1 + anglePerSegment
                val sinEta2 = sin(eta2)
                val cosEta2 = cos(eta2)
                val e2x = cx + (a * cosTheta * cosEta2) - (b * sinTheta * sinEta2)
                val e2y = cy + (a * sinTheta * cosEta2) + (b * cosTheta * sinEta2)
                val ep2x = -a * cosTheta * sinEta2 - b * sinTheta * cosEta2
                val ep2y = -a * sinTheta * sinEta2 + b * cosTheta * cosEta2
                val tanDiff2 = tan((eta2 - eta1) / 2)
                val alpha = sin(eta2 - eta1) * (sqrt(4 + 3 * tanDiff2 * tanDiff2) - 1) / 3
                val q1x = e1x + alpha * ep1x
                val q1y = e1y + alpha * ep1y
                val q2x = e2x - alpha * ep2x
                val q2y = e2y - alpha * ep2y

                // Adding this no-op call to workaround a proguard related issue.
                path?.rLineTo(0f, 0f)
                path?.cubicTo(
                    q1x.toFloat(),
                    q1y.toFloat(),
                    q2x.toFloat(),
                    q2y.toFloat(),
                    e2x.toFloat(),
                    e2y.toFloat()
                )
                eta1 = eta2
                e1x = e2x
                e1y = e2y
                ep1x = ep2x
                ep1y = ep2y
            }
        }
    }

}
#version 150

in vec4 vColor;
in vec2 vLocalPos;

out vec4 fragColor;

uniform vec2 Size;
uniform float Radius;
uniform float GlowSize;
uniform vec4 GlowColor;
uniform bool GlowColorEnable;

// Функция SDF для закругленного прямоугольника
float sdRoundedBox(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + vec2(r);
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r;
}

void main() {
    vec2 size = Size / 2.0;
    float dist = sdRoundedBox(vLocalPos - size, size - GlowSize, Radius);
    float alpha;
    if (GlowSize <= 0 || dist < 0) {
        float dw = fwidth(dist);
        alpha = 1.0 - smoothstep(-dw, dw, dist);
        fragColor = vec4(vColor.rgb, vColor.a * alpha);
    } else {
        alpha = 1.0 - smoothstep(0.0, GlowSize, dist);
        if (GlowColorEnable) {
            fragColor = vec4(GlowColor.rgb, GlowColor.a * alpha);
        } else {
            fragColor = vec4(vColor.rgb, vColor.a * alpha * 0.5F);
        }
    }
    if (fragColor.a < 0.005) {
        discard;
    }
}
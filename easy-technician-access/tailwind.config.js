/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        background: "var(--background)",
        foreground: "var(--foreground)",
        primary: {
          DEFAULT: '#0866E9',
          hover: '#0756c8',
          light: '#e7f0fd',
        },
        navy: {
          DEFAULT: '#0F172A',
          dark: '#090D16',
        }
      },
      borderRadius: {
        '2xl': '1rem',
        '3xl': '1.5rem',
      },
      boxShadow: {
        subtle: '0 4px 20px -2px rgba(8, 102, 233, 0.08)',
        card: '0 2px 12px 0 rgba(0, 0, 0, 0.05)',
      },
    },
  },
  plugins: [],
};

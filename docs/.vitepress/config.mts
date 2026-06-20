import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'Wardove',
  description: 'Personal wardrobe tracker for Android',
  base: '/wardove/',
  lastUpdated: true,

  themeConfig: {
    logo: '/logo.png',

    nav: [
      { text: 'Guide', link: '/guide/getting-started', activeMatch: '/guide/' },
      { text: 'Features', link: '/features/wardrobe', activeMatch: '/features/' },
      { text: 'Reference', link: '/reference/architecture', activeMatch: '/reference/' },
      { text: 'Changelog', link: '/changelog' },
      {
        text: 'GitHub',
        link: 'https://github.com/zorenkonte/wardove',
        target: '_blank',
        rel: 'noopener'
      }
    ],

    sidebar: {
      '/guide/': [
        {
          text: 'Getting Started',
          items: [
            { text: 'Introduction', link: '/guide/getting-started' },
            { text: 'Build from Source', link: '/guide/build' }
          ]
        }
      ],
      '/features/': [
        {
          text: 'Features',
          items: [
            { text: 'Wardrobe', link: '/features/wardrobe' },
            { text: 'Items', link: '/features/item-detail' },
            { text: 'Laundry', link: '/features/laundry' },
            { text: 'Calendar', link: '/features/calendar' },
            { text: 'Stats', link: '/features/stats' },
            { text: 'Settings', link: '/features/settings' }
          ]
        }
      ],
      '/reference/': [
        {
          text: 'Reference',
          items: [
            { text: 'Architecture', link: '/reference/architecture' },
            { text: 'Data Model', link: '/reference/data-model' }
          ]
        }
      ]
    },

    search: {
      provider: 'local'
    },

    editLink: {
      pattern: 'https://github.com/zorenkonte/wardove/edit/main/docs/:path',
      text: 'Edit this page on GitHub'
    },

    footer: {
      message: 'Released under the MIT License.',
      copyright: 'Copyright © 2025-present Wardove'
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/zorenkonte/wardove' }
    ]
  }
})
